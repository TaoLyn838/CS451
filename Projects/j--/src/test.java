public class test {
    public static void main(String[] args) {
         int n = Integer.parseInt(args[0]);
        boolean result = isPrime(n);
        if (result) {
            System.out.println(n + " is a prime number");
         } else {
            System.out.println(n + " is not a prime number");
         }
     }
             // Returns true if n is prime, and false otherwise.
             private static boolean isPrime(int n) {
         if (n < 2) {
              return false;
          }
          for (int i = 2; i <= n / i; i++) {
              if (n % i == 0) {
                  return false;
             }
          }return true;
     }
}
/*
public class test {
    public test();
    Code:
            0: aload_0
       1: invokespecial #1                  // Method java/lang/Object."<init>":()V
            4: return

    public static void main(java.lang.String[]);
    Code:
            0: aload_0
       1: iconst_0
       2: aaload
       3: invokestatic  #7                  // Method java/lang/Integer.parseInt:(Ljava/lang/String;)I
            6: istore_1
       7: iload_1
       8: invokestatic  #13                 // Method isPrime:(I)Z
            11: istore_2
      12: iload_2
      13: ifeq          31
            16: getstatic     #19                 // Field java/lang/System.out:Ljava/io/PrintStream;
            19: iload_1
      20: invokedynamic #25,  0             // InvokeDynamic #0:makeConcatWithConstants:(I)Ljava/lang/String;
            25: invokevirtual #29                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
            28: goto          43
            31: getstatic     #19                 // Field java/lang/System.out:Ljava/io/PrintStream;
            34: iload_1
      35: invokedynamic #35,  0             // InvokeDynamic #1:makeConcatWithConstants:(I)Ljava/lang/String;
            40: invokevirtual #29                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
            43: return
}

 */
