import java.util.ArrayList;

import jminusminus.CLEmitter;

import static jminusminus.CLConstants.*;

/**
 * This class programmatically generates the class file for the following Java application:
 * 
 * <pre>
 * public class IsPrime {
 *     // Entry point.
 *     public static void main(String[] args) {
 *         int n = Integer.parseInt(args[0]);
 *         boolean result = isPrime(n);
 *         if (result) {
 *             System.out.println(n + " is a prime number");
 *         } else {
 *             System.out.println(n + " is not a prime number");
 *         }
 *     }
 *
 *     // Returns true if n is prime, and false otherwise.
 *     private static boolean isPrime(int n) {
 *         if (n < 2) {
 *             return false;
 *         }
 *         for (int i = 2; i <= n / i; i++) {
 *             if (n % i == 0) {
 *                 return false;
 *             }
 *         }
 *         return true;
 *     }
 * }
 * </pre>
 */
public class GenIsPrime {
    public static void main(String[] args) {
        CLEmitter e = new CLEmitter(true);
        ArrayList<String> modifiers = new ArrayList<>();
        // public class IsPrime
        modifiers.add("public");
        e.addClass(modifiers, "IsPrime", "java/lang/Object", null, true);
        // public static void main(String[] args) {
        modifiers.clear();
        modifiers.add("public");
        modifiers.add("static");
        e.addMethod(modifiers, "main", "([Ljava/lang/String;)V", null, true);
        // int n = Integer.parseInt(args[0]);
        e.addNoArgInstruction(ALOAD_0);
        e.addNoArgInstruction(ICONST_0);
        e.addNoArgInstruction(AALOAD);
        e.addMemberAccessInstruction(INVOKESTATIC, "java/lang/Integer", "parseInt",
                "(Ljava/lang/String;)I");
        e.addNoArgInstruction(ISTORE_1);

        //  boolean result = isPrime(n);
        e.addNoArgInstruction(ILOAD_1);
        e.addMemberAccessInstruction(INVOKESTATIC, "IsPrime", "isPrime", "(I)Z");
        e.addNoArgInstruction(ISTORE_2);

        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addNoArgInstruction(ILOAD_1);
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(D)V");

        // if (result)
        // Create an intance (say sb) of StringBuffer on stack for string concatenations
        //    sb = new StringBuffer();


        // return;
        e.addNoArgInstruction(RETURN);

        // private static boolean isPrime(int n)
        modifiers.clear();
        modifiers.add("private");
        modifiers.add("static");
        e.addMethod(modifiers, "isPrime", "(I)Z", null, true);

        // if (n < 2) branch to "Recurse"
        e.addNoArgInstruction(ILOAD_0);
        e.addNoArgInstruction(ICONST_1);
        e.addBranchInstruction(IF_ICMPGE,"A");
        // return false;
        e.addLabel("False");
        e.addNoArgInstruction(ICONST_1);
        e.addNoArgInstruction(IRETURN);

//        for (int i = 2; i <= n / i; i++) {
//            if (n % i == 0) {
//                 return false;
//            }
//          }
        e.addLabel("A");
        e.addLabel("For-loop");
        e.addNoArgInstruction(ICONST_0); // 2
        e.addNoArgInstruction(ISTORE_1); // i = 2
        e.addNoArgInstruction(ILOAD_1); // i
        e.addNoArgInstruction(ILOAD_0); // n
        e.addNoArgInstruction(IDIV);
        e.addBranchInstruction(IFGT, "Failed"); // (n > i)

        e.addLabel("Passed");
        e.addNoArgInstruction(ILOAD_1); // i
        e.addNoArgInstruction(ILOAD_0); // n
        e.addNoArgInstruction(IREM);
        e.addBranchInstruction(IFEQ, "False");
        e.addIINCInstruction(1, 1);
        e.addNoArgInstruction(ICONST_1);
        e.addBranchInstruction(GOTO, "For-loop");

        // return true;
        e.addLabel("Failed");
        e.addNoArgInstruction(ICONST_1);
        e.addNoArgInstruction(IRETURN);


        // Write IsPrime.class to file system
        e.write();
    }
}

/*
e.addBranchInstruction(IFEQ, "NotPrime");
        //    sb = new StringBuffer();
        e.addReferenceInstruction(NEW, "java/lang/StringBuffer"); // StringBuffer sb
        e.addNoArgInstruction(DUP);
        e.addMemberAccessInstruction(INVOKESPECIAL, "java/lang/StringBuffer", "<init>",
                "()V"); // new StringBuffer();
        // sb.append(n);
        e.addNoArgInstruction(ILOAD_1);
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                "(I)Ljava/lang/StringBuffer;");

        e.addLDCInstruction(" is a prime number");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                "(I)Ljava/lang/StringBuffer;");

        // System.out.println(sb.toString());
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer",
                "toString", "()Ljava/lang/String;");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V");

        e.addBranchInstruction(GOTO, "Output");

        e.addLabel("NotPrime");
        //    sb = new StringBuffer();
        e.addReferenceInstruction(NEW, "java/lang/StringBuffer"); // StringBuffer sb
        e.addNoArgInstruction(DUP);
        e.addMemberAccessInstruction(INVOKESPECIAL, "java/lang/StringBuffer", "<init>",
                "()V"); // new StringBuffer();
        // sb.append(n);
        e.addNoArgInstruction(ILOAD_1);
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                "(I)Ljava/lang/StringBuffer;");

        e.addLDCInstruction(" is not a prime number");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                "(I)Ljava/lang/StringBuffer;");

        // System.out.println(sb.toString());
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer",
                "toString", "()Ljava/lang/String;");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V");

        e.addLabel("Output");
        e.addNoArgInstruction(RETURN);
 */
