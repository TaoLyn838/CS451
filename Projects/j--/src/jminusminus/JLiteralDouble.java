// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a double literal.
 */
class JLiteralDouble extends JExpression {
    // String representation of the literal.
    private String text;

    /**
     * Constructs an AST node for a double literal given its line number and string representation.
     *
     * @param line line in which the literal occurs in the source file.
     * @param text string representation of the literal.
     */
    public JLiteralDouble(int line, String text) {
        super(line);
        this.text = text;
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        // TODO
        type = Type.DOUBLE;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        // TODO
        // change input string to long type
        double d = Double.parseDouble(text);
        // if input string equals default value of double (0.0d and 1.0d) send to the CLEmitter instance.
        // else using the idc instruction method from CLEmitter to push a value of type long.
        if (d == 0.0d) {
            output.addNoArgInstruction(DCONST_0);
        } else if (d == 1.0d) {
            output.addNoArgInstruction(DCONST_1);
        } else {
            output.addLDCInstruction(d);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JLiteralDouble:" + line, e);
        e.addAttribute("type", type == null ? "" : type.toString());
        e.addAttribute("value", text);
    }
}
