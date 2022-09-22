package TestF;

public class Try {
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        boolean result = goPrime(n);
        if (result) {
            System.out.println(n + " is a prime number");
        } else {
            System.out.println(n + " is not a prime number");
        }
    }

    // Returns true if n is prime, and false otherwise.
    private static boolean goPrime(int n) {
        if (n < 2) {
            return false;
        }
        for (int i = 2; i <= n/i; i++) {
            if (n % i == 0)
                return false;
        }
        return true;
    }
}
