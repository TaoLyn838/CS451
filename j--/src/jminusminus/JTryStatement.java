// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import org.javacc.parser.TryBlock;

import java.util.ArrayList;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a try-catch-finally statement.
 */
class JTryStatement extends JStatement {
    // The try block.
    private JBlock tryBlock;

    // The catch parameters.
    private ArrayList<JFormalParameter> parameters;

    // The catch blocks.
    private ArrayList<JBlock> catchBlocks;

    private boolean hasCatch = false;
    private boolean hasFinally = false;

    // The finally block.
    private JBlock finallyBlock;

//    private int offset;

    private LocalContext finallyBlockContext;

    /**
     * Constructs an AST node for a try-statement.
     *
     * @param line         line in which the while-statement occurs in the source file.
     * @param tryBlock     the try block.
     * @param parameters   the catch parameters.
     * @param catchBlocks  the catch blocks.
     * @param finallyBlock the finally block.
     */
    public JTryStatement(int line, JBlock tryBlock, ArrayList<JFormalParameter> parameters,
                         ArrayList<JBlock> catchBlocks, JBlock finallyBlock) {
        super(line);
        this.tryBlock = tryBlock;
        this.parameters = parameters;
        this.catchBlocks = catchBlocks;
        this.finallyBlock = finallyBlock;
    }

    /**
     * {@inheritDoc}
     */
    public JTryStatement analyze(Context context) {

        // Check if try statement does not have catch.
        hasCatch = !catchBlocks.isEmpty();
        if (!hasCatch) {
            JAST.compilationUnit.reportSemanticError(line(),
                    "Try statement in Java need at least one catch clause, or a finally clause.");
        }

        // Check if try statement has final lly block.
        hasFinally = (finallyBlock != null);

        // Analyze the try block.
        tryBlock = tryBlock.analyze(context);

        // Analyze each catch block in a new LocalContext created from context as the parent
        //   the catch parameter must be declared in this new context.
        LocalContext catchContext;

        for (int i = 0; i < catchBlocks.size(); i++) {

            // Initialize a new LocalContext with the parent context as its parent
            catchContext = new LocalContext(context);

            // Declare the catch parameter in the new LocalContext
            JFormalParameter catchParam = parameters.get(i);

            //based on JMethodDeclaration, preAnalyze param resolve
            catchParam.setType(catchParam.type().resolve(catchContext));

//            IDefn definition = new LocalVariableDefn(catchParam.type(), catchContext.nextOffset());
            LocalVariableDefn definition = new LocalVariableDefn(catchParam.type(), catchContext.nextOffset());
            definition.initialize();
            catchContext.addEntry(catchParam.line(), catchParam.name(), definition);
            JBlock jBlock = catchBlocks.get(i).analyze(catchContext);
            catchBlocks.set(i, jBlock);

        }


        // Analyze the optional finally block in a new LocalContext created from context as the parent.
        if (hasFinally) {
            catchContext = new LocalContext(context);

            // Get offset for finally block
            // this.offset = catchContext.offset();

            // Get context for the finallyblock
            finallyBlockContext = catchContext;

            finallyBlock = finallyBlock.analyze(catchContext);

        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        String startTryLabel = output.createLabel();
        output.addLabel(startTryLabel);

        tryBlock.codegen(output);

        String endFinallyLabel = output.createLabel();
        String endTryLabel = output.createLabel();

        // Optional finally block
        if (hasFinally) {

            // Generate code for the optional finally block
            finallyBlock.codegen(output);

            // an unconditional jump to an “end finally” label
            output.addBranchInstruction(GOTO, endFinallyLabel);

        }
        // Add an “end try” label
        output.addLabel(endTryLabel);

        ArrayList<String> catchLabels = new ArrayList<>();

        // Initialize startFinallyLabel
        String startFinallyLabel = output.createLabel();

        // Case 2: try {...} catch_1 {...} to catch_n {...}
        for (int i = 0; i < catchBlocks.size(); i++) {

            // Add a “start catch” label
            String startCatchLabel = output.createLabel();
            catchLabels.add(startCatchLabel);

            // generate code for the catch block
            JBlock catchBlock = catchBlocks.get(i);

            // Add an exception handler with the appropriate argument
            output.addExceptionHandler(startTryLabel, endTryLabel, startCatchLabel,
                    parameters.get(catchBlocks.indexOf(catchBlock)).type().jvmName());

            output.addLabel(startCatchLabel);

            // Generate code to store the catch variable
            output.addNoArgInstruction(ASTORE_1);

////            // Add “end catch” label
//            String endCatchLabel = output.createLabel();
//            output.addLabel(endCatchLabel);

            catchBlock.codegen(output);
            // Optional finally block
            if (hasFinally) {
                // Generate code for the optional finally block
                finallyBlock.codegen(output);
            }
            // an unconditional jump to an “end finally” label
            output.addBranchInstruction(GOTO, endFinallyLabel);
        }

        // Initialize startFinallyPlusOneLabel
        String startFinallyPlusOneLabel = output.createLabel();

        //  For the optional finally block
        if (hasFinally) {
            // add a “start finally” label
            output.addLabel(startFinallyLabel);

            // generate an ASTORE instruction with the offset o obtained from the context for the finallyBlock
            output.addOneArgInstruction(ASTORE, finallyBlockContext.offset());

            // add a “start finally plus one” label
            output.addLabel(startFinallyPlusOneLabel);

            // generate code for the finallyBlock
            finallyBlock.codegen(output);

            // Generate an ALOAD instruction with the offset o and an ATHROW instruction
            output.addOneArgInstruction(ALOAD, finallyBlockContext.offset());
            output.addNoArgInstruction(ATHROW);

        }

        // Add endFinallyLabel
        output.addLabel(endFinallyLabel);

        // Add an exception handler with arguments “start try”, “end try”, “start finally”, and null
        output.addExceptionHandler(startTryLabel, endTryLabel, startFinallyLabel, null);

        // for each catch block
        for (int i = 0; i < catchLabels.size(); i++) {

            // Add an exception handler with the arguments “start catch”, “end catch”, “start finally”, and null;
            if (i < catchLabels.size() - 1) {
                output.addExceptionHandler(catchLabels.get(i), catchLabels.get(i + 1), startFinallyLabel, null);
            } else {

                //catch (...) {...} <---> finally {...}
                output.addExceptionHandler(catchLabels.get(i), startFinallyLabel, startFinallyLabel, null);
            }
        }

        if (hasFinally) {
            // Add an exception handler with the arguments “start finally”, “start
            //  finally plus one”, “start finally”, and null
            output.addExceptionHandler(startFinallyLabel, startFinallyPlusOneLabel, startFinallyLabel, null);
        }


    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JTryStatement:" + line, e);
        JSONElement e1 = new JSONElement();
        e.addChild("TryBlock", e1);
        tryBlock.toJSON(e1);
        if (catchBlocks != null) {
            for (int i = 0; i < catchBlocks.size(); i++) {
                JFormalParameter param = parameters.get(i);
                JBlock catchBlock = catchBlocks.get(i);
                JSONElement e2 = new JSONElement();
                e.addChild("CatchBlock", e2);
                String s = String.format("[\"%s\", \"%s\"]", param.name(), param.type() == null ?
                        "" : param.type().toString());
                e2.addAttribute("parameter", s);
                catchBlock.toJSON(e2);
            }
        }
        if (finallyBlock != null) {
            JSONElement e2 = new JSONElement();
            e.addChild("FinallyBlock", e2);
            finallyBlock.toJSON(e2);
        }
    }
}
