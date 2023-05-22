// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a switch-statement.
 */
public class JSwitchStatement extends JStatement {
    // Test expression.
    private JExpression condition;

    // List of switch-statement groups.
    private ArrayList<SwitchStatementGroup> stmtGroup;

    // Anayze the case expressions and make sure they are integer literals.
    private int lo; // the lowest case label value
    private int hi; // the highest case label value
    private ArrayList<Integer> ascendingArrL;
    private int nLabels; // the total real case labels in the switch statement

    boolean hasBreak;
    String breakLabel;

    // set the default label to false
    boolean hasDefault = false;

    /**
     * Constructs an AST node for a switch-statement.
     *
     * @param line      line in which the switch-statement occurs in the source file.
     * @param condition test expression.
     * @param stmtGroup list of statement groups.
     */
    public JSwitchStatement(int line, JExpression condition,
                            ArrayList<SwitchStatementGroup> stmtGroup) {
        super(line);
        this.condition = condition;
        this.stmtGroup = stmtGroup;
    }

    /**
     * {@inheritDoc}
     */
    public JStatement analyze(Context context) {
        // Analyze the condition and make sure it is an integer
        condition = condition.analyze(context);
        condition.type().mustMatchExpected(line(), Type.INT);

        // Analyze the statements in each case group in the new context
        LocalContext statement;

        // init the label counting
        nLabels = 0;

        ascendingArrL = new ArrayList<>();

        // Anayze the case expressions and make sure they are integer literals.
        for (SwitchStatementGroup switchStatement: stmtGroup) {
            statement = new LocalContext(context);
            for (JExpression caseInSwitch: switchStatement.getSwitchLabels()) {
                // check case default and skip it if here is it.
                if (caseInSwitch != null) {
                    caseInSwitch.analyze(statement);
                    caseInSwitch.type().mustMatchExpected(line(), Type.INT);
                    ascendingArrL.add(((JLiteralInt) caseInSwitch).toInt());
                    nLabels++;
                }
            }

            for (JStatement block: switchStatement.getBlock()) {
                if (block instanceof JBreakStatement) {
                    JMember.enclosingStatement.push(this);
                }
                block.analyze(statement);
            }
        }
        // sort and init low and high value in switch statement
        Collections.sort(ascendingArrL);
        lo = ascendingArrL.get(0);
        hi = ascendingArrL.get(ascendingArrL.size()-1);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        condition.codegen(output);
        breakLabel = output.createLabel();
        String defaultLabel = output.createLabel();
        switch (isTableOrLookUp()) {
            case TABLESWITCH:
                ArrayList<String> labels = new ArrayList<>();
                for (SwitchStatementGroup switchStatement: stmtGroup) {
                    for (JExpression caseInSwitch : switchStatement.getSwitchLabels()) {
                        if (caseInSwitch != null) {
                            labels.add(output.createLabel());
                        } else {
                            output.addLabel(defaultLabel);
                            hasDefault = true;
                        }
                    }
                }
                output.addTABLESWITCHInstruction(defaultLabel, lo, hi, labels);

                int labelsIndex = 0;
                for (SwitchStatementGroup tempGroup : stmtGroup) {
                    for (JExpression switchLabel : tempGroup.getSwitchLabels()) {
                        if (switchLabel != null) {
                            output.addLabel(labels.get(labelsIndex));
                        }
                        labelsIndex++;
                    }

                    for (JStatement statement : tempGroup.getBlock()) {
                        statement.codegen(output);
                    }
                }

                if (!hasDefault) output.addLabel(defaultLabel);

            case LOOKUPSWITCH:
                TreeMap<Integer, String> matchLabelPairs = new TreeMap<>();
                for (SwitchStatementGroup switchStatement: stmtGroup) {
                    for (JExpression caseInSwitch : switchStatement.getSwitchLabels()) {
                        if (caseInSwitch != null) {
                            int key = ((JLiteralInt) caseInSwitch).toInt();
                            matchLabelPairs.put(key, output.createLabel());
                        } else {
                            output.addLabel(defaultLabel);
                            hasDefault = true;
                        }
                    }
                }

                output.addLOOKUPSWITCHInstruction(defaultLabel, matchLabelPairs.size(), matchLabelPairs);

                for (SwitchStatementGroup tempGroup : stmtGroup) {
                    for (JExpression switchLabel : tempGroup.getSwitchLabels()) {
                        if (switchLabel == null) {
                            output.addLabel(defaultLabel);
                            hasDefault = true;
                        } else {
                            int key = ((JLiteralInt) switchLabel).toInt();
                            output.addLabel(matchLabelPairs.get(key));
                        }
                    }
                    for (JStatement statement : tempGroup.getBlock()) {
                        statement.codegen(output);
                    }
                }
                if (!hasDefault) output.addLabel(defaultLabel);
        }
        if (hasBreak) output.addLabel(breakLabel);
    }

    /**
     * The algorithm for codegen to decide which instruction
     *  (TABLESWITCH or LOOKUPSWITCH) to emit using the above heuristic.
     */
    private int isTableOrLookUp() {
        long tableSpaceCost = 5 + hi - lo;
        long tableTimeCost = 3;
        long lookupSpaceCost = 3 + 2 * nLabels;
        long lookupTimeCost = nLabels;
        return nLabels > 0 && (tableSpaceCost + 3 * tableTimeCost <= lookupSpaceCost + 3 * lookupTimeCost) ?
                TABLESWITCH : LOOKUPSWITCH;
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JSwitchStatement:" + line, e);
        JSONElement e1 = new JSONElement();
        e.addChild("Condition", e1);
        condition.toJSON(e1);
        for (SwitchStatementGroup group : stmtGroup) {
            group.toJSON(e);
        }
    }
}

/**
 * A switch statement group consists of case labels and a block of statements.
 */
class SwitchStatementGroup {
    // Case labels.
    private ArrayList<JExpression> switchLabels;

    // Block of statements.
    private ArrayList<JStatement> block;

    /**
     * Constructs a switch-statement group.
     *
     * @param switchLabels case labels.
     * @param block        block of statements.
     */
    public SwitchStatementGroup(ArrayList<JExpression> switchLabels, ArrayList<JStatement> block) {
        this.switchLabels = switchLabels;
        this.block = block;
    }

    public ArrayList<JExpression> getSwitchLabels() {
        return switchLabels;
    }
    public ArrayList<JStatement> getBlock() {
        return block;
    }

    /**
     * Stores information about this switch statement group in JSON format.
     *
     * @param json the JSON emitter.
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("SwitchStatementGroup", e);
        for (JExpression label : switchLabels) {
            JSONElement e1 = new JSONElement();
            if (label != null) {
                e.addChild("Case", e1);
                label.toJSON(e1);
            } else {
                e.addChild("Default", e1);
            }
        }
        if (block != null) {
            for (JStatement stmt : block) {
                stmt.toJSON(e);
            }
        }
    }
}
