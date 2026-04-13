import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Main entry point for the GutenbergPubs management console application.
 *
 * This class initializes the database connection and presents a
 * main menu for selecting different publication workflows.
 */
public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Connection conn = null;

        System.out.println("======================================");
        System.out.println("=== GutenbergPubs Management System ===");
        System.out.println("======================================");

        try {
            conn = DBConnection.getConnection();

            if (conn == null || conn.isClosed()) {
                System.out.println("[ERROR] Database connection failed.");
                System.out.println("Check your credentials and database server.");
                return;
            }

            System.out.println("Database connected successfully.\n");

            // Initialize operation modules with shared connection and input scanner
            Operation1 op1 = new Operation1(conn, scanner);
            Operation2 op2 = new Operation2(conn, scanner);
            Operation3 op3 = new Operation3(conn, scanner);
            Operation4 op4 = new Operation4(conn, scanner);

            boolean running = true;

            while (running) {
                printMainMenu();
                System.out.print("Enter choice: ");
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        op1.runMenu();
                        break;
                    case "2":
                        op2.runMenu();
                        break;
                    case "3":
                        op3.runMenu();
                        break;
                    case "4":
                        op4.runMenu();
                        break;
                    case "5":
                        running = false;
                        System.out.println("Exiting system...");
                        break;
                    default:
                        System.out.println("[ERROR] Invalid option.");
                }

                System.out.println();
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                    System.out.println("Database connection closed.");
                }
            } catch (SQLException e) {
                System.out.println("[ERROR] Failed to close connection.");
            }

            scanner.close();
        }
    }

    /**
     * Display the top-level menu for the different workflow sections.
     */
    private static void printMainMenu() {
        System.out.println("1. Editing and Publishing");
        System.out.println("2. Production of Editions/Issues");
        System.out.println("3. Distribution");
        System.out.println("4. Reports");
        System.out.println("5. Exit");
    }
}