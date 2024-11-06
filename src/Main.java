import java.util.Scanner;

public class Main {
    public static Scanner scanner;
    static {
        scanner = new Scanner(System.in);
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        System.out.println("<------WELCOME TO SVNIT PORTAL------>");
        System.out.println("Enter your role :");
        System.out.println("1-Student");
        System.out.println("2-Professor");
        System.out.println("3-Administrator");
        System.out.println("4-Exit");
        System.out.println("----------------------------------");
        int role = scanner.nextInt();
        if (role == 4) {
            System.exit(0);
        }
        System.out.println("Enter your email:");
        String email = scanner.next();
        System.out.println("Enter your password:");
        String password = scanner.next();
        System.out.println("----------------------------------");

        switch (role) {

            case 1:
                Users student = new Student(email, password);
                break;

            case 2:
                Users professor = new Professor(email, password);
                break;

            case 3:
                Users administrator = new Administrator(email, password);
                break;
            default:
                System.out.println("Invalid input try again");
                System.out.println("----------------------------------");
                main(args);
                break;
        }
    }
}
