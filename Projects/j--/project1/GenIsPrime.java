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
        e.addNoArgInstruction(ISTORE_1); // store n

        //  boolean result = isPrime(n);
        e.addNoArgInstruction(ILOAD_1);
        e.addMemberAccessInstruction(INVOKESTATIC, "IsPrime", "isPrime", "(I)Z");
        e.addNoArgInstruction(ISTORE_2); // result

        // if (result)
        e.addNoArgInstruction(ILOAD_2); // result
        e.addBranchInstruction(IFEQ, "Else");
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        //    sb = new StringBuffer();
        e.addReferenceInstruction(NEW, "java/lang/StringBuffer");
        e.addNoArgInstruction(DUP);
        e.addMemberAccessInstruction(INVOKESPECIAL, "java/lang/StringBuffer", "<init>", "()V");

        e.addNoArgInstruction(ILOAD_1); // n
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                "(I)Ljava/lang/StringBuffer;");
        // append "str"
        e.addLDCInstruction(" is a prime number");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuffer;");

        // System.out.println(sb.toString());
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer",
                "toString", "()Ljava/lang/String;");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V");
        e.addBranchInstruction(GOTO, "SOUT");

        e.addLabel("Else");
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addReferenceInstruction(NEW, "java/lang/StringBuffer"); // StringBuffer sb
        e.addNoArgInstruction(DUP);
        e.addMemberAccessInstruction(INVOKESPECIAL, "java/lang/StringBuffer", "<init>",
                "()V"); // new StringBuffer();
        e.addNoArgInstruction(ILOAD_1); // n
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                "(I)Ljava/lang/StringBuffer;");
        // append "str"
        e.addLDCInstruction(" is not a prime number");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuffer;");

        // System.out.println(sb.toString());
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer",
                "toString", "()Ljava/lang/String;");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V");

        e.addLabel("SOUT");
        // return;
        e.addNoArgInstruction(RETURN);

        // private static boolean isPrime(int n)
        modifiers.clear();
        modifiers.add("private");
        modifiers.add("static");
        e.addMethod(modifiers, "isPrime", "(I)Z", null, true);

        // if (n < 2)
        e.addNoArgInstruction(ILOAD_0); // n
        e.addNoArgInstruction(ICONST_2); // 2
        e.addBranchInstruction(IF_ICMPGE, "GoLoop");
        //return base case
        e.addNoArgInstruction(ICONST_0);
        e.addNoArgInstruction(IRETURN);

        e.addLabel("GoLoop");
        // for (int i = 2; i <= n / i; i++)
        e.addNoArgInstruction(ICONST_2);
        e.addNoArgInstruction(ISTORE_3); // i = 2
        // n / i

        e.addLabel("Looping");
        e.addNoArgInstruction(ILOAD_3);
        e.addNoArgInstruction(ILOAD_0); // n
        e.addNoArgInstruction(ILOAD_3);
        e.addNoArgInstruction(IDIV); // n / i

        // i >  n / i
        e.addBranchInstruction(IF_ICMPGT, "GoTrue");

        // if (n % i == 0)
        e.addNoArgInstruction(ILOAD_0);
        e.addNoArgInstruction(ILOAD_3);
        e.addNoArgInstruction(IREM);
        // n / i != 0
        e.addBranchInstruction(IFNE, "i++");
        e.addNoArgInstruction(ICONST_0);
        e.addNoArgInstruction(IRETURN);

        e.addLabel("i++");
        e.addNoArgInstruction(ILOAD_3);
        e.addNoArgInstruction(ICONST_1);
//        e.addIINCInstruction(0, 1);
        e.addNoArgInstruction(IADD);
        e.addNoArgInstruction(ISTORE_3);
        e.addBranchInstruction(GOTO, "Looping");

        e.addLabel("GoTrue");
        e.addNoArgInstruction(ICONST_1);
        e.addNoArgInstruction(IRETURN);

        // Write IsPrime.class to file system
        e.write();
    }
}
