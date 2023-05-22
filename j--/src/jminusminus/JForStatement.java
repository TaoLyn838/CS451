// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a for-statement.
 */
class JForStatement extends JStatement {
    // Initialization.
    private ArrayList<JStatement> init;

    // Test expression
    private JExpression condition;

    // Update.
    private ArrayList<JStatement> update;

    // The body.
    private JStatement body;

    public boolean hasBreak;
    public String breakLabel;
    public boolean hasContinue;
    public String continueLabel;


    /**
     * Constructs an AST node for a for-statement.
     *
     * @param line      line in which the for-statement occurs in the source file.
     * @param init      the initialization.
     * @param condition the test expression.
     * @param update    the update.
     * @param body      the body.
     */
    public JForStatement(int line, ArrayList<JStatement> init, JExpression condition,
                         ArrayList<JStatement> update, JStatement body) {
        super(line);
        this.init = init;
        this.condition = condition;
        this.update = update;
        this.body = body;
    }

    /**
     * {@inheritDoc}
     */
    public JForStatement analyze(Context context) {
        JMember.enclosingStatement.push(this);

        // Analyze the init in the new context.
        if (init != null) {
            for (JStatement tempStatement : init) {
                tempStatement.analyze(context);
            }
        }

        // Analyze the condition in the new context and make sure it’s a boolean.
        if (condition != null) {
            condition = condition.analyze(context);
            condition.type().mustMatchExpected(line(), Type.BOOLEAN);
        }

        // Analyze the update in the new context.
        if (update != null) {
            for (JStatement tempStatement : update) {
                tempStatement.analyze(context);
            }
        }
        body = (JStatement) body.analyze(context);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        if (hasBreak) breakLabel = output.createLabel();
        if (hasContinue) continueLabel = output.createLabel();
        String bodyLabel = output.createLabel();
        String endLabel = output.createLabel();
        if (init != null) {
            for (JStatement statement : init) {
                statement.codegen(output);
            }
        }
        output.addLabel(bodyLabel);
        if (condition != null) {
            condition.codegen(output, endLabel, false);
        }
        body.codegen(output);
        if (hasContinue) {
            output.addLabel(continueLabel);
        }
        if (update != null) {
            for (JStatement statement : update) {
                statement.codegen(output);
            }
        }
        output.addBranchInstruction(GOTO, bodyLabel);
        output.addLabel(endLabel);
        if (hasBreak) {
            output.addLabel(breakLabel);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JForStatement:" + line, e);
        if (init != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Init", e1);
            for (JStatement stmt : init) {
                stmt.toJSON(e1);
            }
        }
        if (condition != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Condition", e1);
            condition.toJSON(e1);
        }
        if (update != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Update", e1);
            for (JStatement stmt : update) {
                stmt.toJSON(e1);
            }
        }
        if (body != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Body", e1);
            body.toJSON(e1);
        }
    }
}
