import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Operation4 {

    private final Connection conn;
    private final Scanner scanner;

    public Operation4(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    public void runMenu() {
        boolean back = false;

        while (!back) {
            printMenu();
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "4A":
                case "1":
                    copiesAndTotalPricePerDistributor();
                    break;
                case "4B":
                case "2":
                    copiesAndTotalPricePerWeek();
                    break;
                case "4C":
                case "3":
                    copiesAndTotalPricePerMonth();
                    break;
                case "4D":
                case "4":
                    totalRevenue();
                    break;
                case "4E":
                case "5":
                    totalExpenses();
                    break;
                case "4F":
                case "6":
                    totalCurrentDistributors();
                    break;
                case "4G":
                case "7":
                    totalRevenuePerCity();
                    break;
                case "4H":
                case "8":
                    totalRevenuePerDistributor();
                    break;
                case "4I":
                case "9":
                    totalRevenuePerCountry();
                    break;
                case "4J":
                case "10":
                    totalPaymentsPerMonth();
                    break;
                case "4K":
                case "11":
                    totalPaymentsByWorkType();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("[ERROR] Invalid option.");
            }

            System.out.println();
        }
    }

    private void printMenu() {
        System.out.println("==================================");
        System.out.println("=== Reports ======================");
        System.out.println("==================================");
        System.out.println("4A. Copies and total price per distributor");
        System.out.println("4B. Copies and total price per week");
        System.out.println("4C. Copies and total price per month");
        System.out.println("4D. Total revenue of publishing house");
        System.out.println("4E. Total expenses of publishing house");
        System.out.println("4F. Total number of current distributors");
        System.out.println("4G. Total revenue per city");
        System.out.println("4H. Total revenue per distributor");
        System.out.println("4I. Total revenue per country");
        System.out.println("4J. Total payments to editors/authors per month");
        System.out.println("4K. Total payments by work type");
        System.out.println("0. Back");
    }

    private void copiesAndTotalPricePerDistributor() {
        String sql =
                "SELECT d.id AS distributor_id, " +
                "       d.name AS distributor_name, " +
                "       ei.publication_id, " +
                "       SUM(o.quantity) AS total_copies, " +
                "       SUM(o.quantity * o.unit_price) AS total_price " +
                "FROM Orders o " +
                "JOIN Distributors d ON o.distributor_id = d.id " +
                "JOIN EditionIssue ei ON o.edition_issue_id = ei.id " +
                "GROUP BY d.id, d.name, ei.publication_id " +
                "ORDER BY d.id, ei.publication_id";

        runAndPrintQuery(sql);
    }

    private void copiesAndTotalPricePerWeek() {
        String sql =
                "SELECT YEAR(o.order_date) AS report_year, " +
                "       WEEK(o.order_date) AS report_week, " +
                "       ei.publication_id, " +
                "       SUM(o.quantity) AS total_copies, " +
                "       SUM(o.quantity * o.unit_price) AS total_price " +
                "FROM Orders o " +
                "JOIN EditionIssue ei ON o.edition_issue_id = ei.id " +
                "GROUP BY YEAR(o.order_date), WEEK(o.order_date), ei.publication_id " +
                "ORDER BY report_year, report_week, ei.publication_id";

        runAndPrintQuery(sql);
    }

    private void copiesAndTotalPricePerMonth() {
        String sql =
                "SELECT YEAR(o.order_date) AS report_year, " +
                "       MONTH(o.order_date) AS report_month, " +
                "       ei.publication_id, " +
                "       SUM(o.quantity) AS total_copies, " +
                "       SUM(o.quantity * o.unit_price) AS total_price " +
                "FROM Orders o " +
                "JOIN EditionIssue ei ON o.edition_issue_id = ei.id " +
                "GROUP BY YEAR(o.order_date), MONTH(o.order_date), ei.publication_id " +
                "ORDER BY report_year, report_month, ei.publication_id";

        runAndPrintQuery(sql);
    }

    private void totalRevenue() {
        String sql =
                "SELECT COALESCE(SUM(total_billed_amount), 0) AS total_revenue " +
                "FROM Orders";

        runAndPrintQuery(sql);
    }

    private void totalExpenses() {
        String sql =
                "SELECT " +
                "    (SELECT COALESCE(SUM(ship_cost), 0) FROM Orders) + " +
                "    (SELECT COALESCE(SUM(amount), 0) FROM UserPayments) AS total_expenses";

        runAndPrintQuery(sql);
    }

    private void totalCurrentDistributors() {
        String sql =
                "SELECT COUNT(*) AS total_current_distributors " +
                "FROM Distributors";

        runAndPrintQuery(sql);
    }

    private void totalRevenuePerCity() {
        String sql =
                "SELECT d.city, " +
                "       COALESCE(SUM(o.total_billed_amount), 0) AS revenue_per_city " +
                "FROM Distributors d " +
                "LEFT JOIN Orders o ON d.id = o.distributor_id " +
                "GROUP BY d.city " +
                "ORDER BY revenue_per_city DESC";

        runAndPrintQuery(sql);
    }

    private void totalRevenuePerDistributor() {
        String sql =
                "SELECT d.id AS distributor_id, " +
                "       d.name, " +
                "       COALESCE(SUM(o.total_billed_amount), 0) AS revenue_per_distributor " +
                "FROM Distributors d " +
                "LEFT JOIN Orders o ON d.id = o.distributor_id " +
                "GROUP BY d.id, d.name " +
                "ORDER BY revenue_per_distributor DESC";

        runAndPrintQuery(sql);
    }

    private void totalRevenuePerCountry() {
        String sql =
                "SELECT d.country, " +
                "       COALESCE(SUM(o.total_billed_amount), 0) AS revenue_per_location " +
                "FROM Distributors d " +
                "LEFT JOIN Orders o ON d.id = o.distributor_id " +
                "GROUP BY d.country " +
                "ORDER BY revenue_per_location DESC";

        runAndPrintQuery(sql);
    }

    private void totalPaymentsPerMonth() {
        String sql =
                "SELECT YEAR(issue_date) AS pay_year, " +
                "       MONTH(issue_date) AS pay_month, " +
                "       SUM(amount) AS total_payments " +
                "FROM UserPayments " +
                "GROUP BY YEAR(issue_date), MONTH(issue_date) " +
                "ORDER BY pay_year, pay_month";

        runAndPrintQuery(sql);
    }

    private void totalPaymentsByWorkType() {
        String sql =
                "SELECT payment_type, " +
                "       SUM(amount) AS total_payments_by_work_type " +
                "FROM UserPayments " +
                "GROUP BY payment_type " +
                "ORDER BY payment_type";

        runAndPrintQuery(sql);
    }

    private void runAndPrintQuery(String sql) {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            printResultSetAsTable(rs);
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void printResultSetAsTable(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        List<String[]> rows = new ArrayList<>();
        int[] widths = new int[columnCount];

        for (int i = 0; i < columnCount; i++) {
            widths[i] = meta.getColumnLabel(i + 1).length();
        }

        while (rs.next()) {
            String[] row = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                String value = rs.getString(i + 1);
                if (value == null) {
                    value = "NULL";
                }
                row[i] = value;
                widths[i] = Math.max(widths[i], value.length());
            }
            rows.add(row);
        }

        if (rows.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        printSeparator(widths);

        for (int i = 0; i < columnCount; i++) {
            System.out.printf("| %-" + widths[i] + "s ", meta.getColumnLabel(i + 1));
        }
        System.out.println("|");

        printSeparator(widths);

        for (String[] row : rows) {
            for (int i = 0; i < columnCount; i++) {
                System.out.printf("| %-" + widths[i] + "s ", row[i]);
            }
            System.out.println("|");
        }

        printSeparator(widths);
    }

    private void printSeparator(int[] widths) {
        for (int width : widths) {
            System.out.print("+-");
            for (int i = 0; i < width; i++) {
                System.out.print("-");
            }
            System.out.print("-");
        }
        System.out.println("+");
    }
}