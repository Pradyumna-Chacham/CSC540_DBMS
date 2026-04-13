import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * Menu-driven class for distribution workflow management.
 *
 * This class handles distributors, orders, billing, distributor payments,
 * and reports about billing mismatches or distributor revenue.
 */
public class Operation3 {

    private final Connection conn;
    private final Scanner scanner;

    public Operation3(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    /**
     * Run the Distribution submenu until the user chooses to go back.
     */
    public void runMenu() {
        boolean back = false;

        while (!back) {
            printMenu();
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "3A":
                case "1":
                    enterDistributor();
                    break;
                case "3B":
                case "2":
                    updateDistributor();
                    break;
                case "3C":
                case "3":
                    deleteDistributor();
                    break;
                case "3D":
                case "4":
                    inputOrder();
                    break;
                case "3E":
                case "5":
                    billDistributor();
                    break;
                case "3F":
                case "6":
                    recordPayment();
                    break;
                case "3G":
                case "7":
                    findMismatchedDistributors();
                    break;
                case "3H":
                case "8":
                    listDistributorsByTypeCity();
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

    /**
     * Print the Distribution menu options.
     */
    private void printMenu() {
        System.out.println("==================================");
        System.out.println("=== Distribution =================");
        System.out.println("==================================");
        System.out.println("3A. Enter new distributor");
        System.out.println("3B. Update distributor information");
        System.out.println("3C. Delete distributor");
        System.out.println("3D. Input order from distributor");
        System.out.println("3E. Bill distributor for an order");
        System.out.println("3F. Record distributor payment");
        System.out.println("3G. Identify distributors with billing mismatches");
        System.out.println("3H. List distributors by type and city");
        System.out.println("0. Back");
    }

    /**
     * Prompt for distributor details and insert a new distributor entry.
     */
    private void enterDistributor() {
        try {
            System.out.print("Name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Address: ");
            String address = scanner.nextLine().trim();

            System.out.print("Type (BOOKSTORE/WHOLESALE/LIBRARY): ");
            String type = scanner.nextLine().trim().toUpperCase();

            System.out.print("Phone number: ");
            String phoneNumber = scanner.nextLine().trim();

            System.out.print("Contact name: ");
            String contactName = scanner.nextLine().trim();

            System.out.print("City: ");
            String city = scanner.nextLine().trim();

            System.out.print("Country: ");
            String country = scanner.nextLine().trim();

            if (name.isEmpty() || address.isEmpty() || phoneNumber.isEmpty() ||
                contactName.isEmpty() || city.isEmpty() || country.isEmpty()) {
                System.out.println("[ERROR] All distributor fields are required.");
                return;
            }

            if (!isValidDistributorType(type)) {
                System.out.println("[ERROR] Invalid distributor type.");
                return;
            }

            if (phoneNumberExists(phoneNumber)) {
                System.out.println("[ERROR] Phone number already exists.");
                return;
            }

            String id = UUID.randomUUID().toString();
            String sql = "INSERT INTO Distributors " +
                    "(id, name, address, type, phone_number, contact_name, balance, city, country) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, id);
                ps.setString(2, name);
                ps.setString(3, address);
                ps.setString(4, type);
                ps.setString(5, phoneNumber);
                ps.setString(6, contactName);
                ps.setDouble(7, 0.00);
                ps.setString(8, city);
                ps.setString(9, country);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("[SUCCESS] Distributor created.");
                    System.out.println("Distributor ID: " + id);
                } else {
                    System.out.println("[ERROR] Distributor was not created.");
                }
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Update distributor contact and location information.
     */
    private void updateDistributor() {
        System.out.print("Distributor ID: ");
        String distributorId = scanner.nextLine().trim();

        if (!distributorExists(distributorId)) {
            System.out.println("[ERROR] Distributor not found.");
            return;
        }

        try {
            String currentName = getDistributorValue(distributorId, "name");
            String currentAddress = getDistributorValue(distributorId, "address");
            String currentPhone = getDistributorValue(distributorId, "phone_number");
            String currentContact = getDistributorValue(distributorId, "contact_name");
            String currentCity = getDistributorValue(distributorId, "city");
            String currentCountry = getDistributorValue(distributorId, "country");

            System.out.println("Leave blank to keep current value.");

            System.out.print("New name [" + nullSafe(currentName) + "]: ");
            String newName = scanner.nextLine().trim();

            System.out.print("New address [" + nullSafe(currentAddress) + "]: ");
            String newAddress = scanner.nextLine().trim();

            System.out.print("New phone number [" + nullSafe(currentPhone) + "]: ");
            String newPhone = scanner.nextLine().trim();

            System.out.print("New contact name [" + nullSafe(currentContact) + "]: ");
            String newContact = scanner.nextLine().trim();

            System.out.print("New city [" + nullSafe(currentCity) + "]: ");
            String newCity = scanner.nextLine().trim();

            System.out.print("New country [" + nullSafe(currentCountry) + "]: ");
            String newCountry = scanner.nextLine().trim();

            String finalName = newName.isEmpty() ? currentName : newName;
            String finalAddress = newAddress.isEmpty() ? currentAddress : newAddress;
            String finalPhone = newPhone.isEmpty() ? currentPhone : newPhone;
            String finalContact = newContact.isEmpty() ? currentContact : newContact;
            String finalCity = newCity.isEmpty() ? currentCity : newCity;
            String finalCountry = newCountry.isEmpty() ? currentCountry : newCountry;

            if (finalName == null || finalName.trim().isEmpty() ||
                finalAddress == null || finalAddress.trim().isEmpty() ||
                finalPhone == null || finalPhone.trim().isEmpty() ||
                finalContact == null || finalContact.trim().isEmpty() ||
                finalCity == null || finalCity.trim().isEmpty() ||
                finalCountry == null || finalCountry.trim().isEmpty()) {
                System.out.println("[ERROR] Required distributor fields cannot be empty.");
                return;
            }

            if (!finalPhone.equals(currentPhone) && phoneNumberExists(finalPhone)) {
                System.out.println("[ERROR] Phone number already exists.");
                return;
            }

            String sql = "UPDATE Distributors SET name = ?, address = ?, phone_number = ?, " +
                    "contact_name = ?, city = ?, country = ? WHERE id = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, finalName);
                ps.setString(2, finalAddress);
                ps.setString(3, finalPhone);
                ps.setString(4, finalContact);
                ps.setString(5, finalCity);
                ps.setString(6, finalCountry);
                ps.setString(7, distributorId);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("[SUCCESS] Distributor updated.");
                } else {
                    System.out.println("[ERROR] No distributor updated.");
                }
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Delete a distributor when there are no related orders or payments.
     */
    private void deleteDistributor() {
        System.out.print("Distributor ID: ");
        String distributorId = scanner.nextLine().trim();

        if (!distributorExists(distributorId)) {
            System.out.println("[ERROR] Distributor not found.");
            return;
        }

        if (ordersExistForDistributor(distributorId)) {
            System.out.println("[ERROR] Cannot delete distributor because related Orders rows exist.");
            return;
        }

        if (distPaymentsExistForDistributor(distributorId)) {
            System.out.println("[ERROR] Cannot delete distributor because related DistPayment rows exist.");
            return;
        }

        String sql = "DELETE FROM Distributors WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, distributorId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("[SUCCESS] Distributor deleted.");
            } else {
                System.out.println("[ERROR] No distributor deleted.");
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Record a new distributor order for a published edition or issue.
     */
    private void inputOrder() {
        try {
            System.out.print("Distributor ID: ");
            String distributorId = scanner.nextLine().trim();

            if (!distributorExists(distributorId)) {
                System.out.println("[ERROR] Distributor not found.");
                return;
            }

            System.out.print("Edition/Issue ID: ");
            String editionIssueId = scanner.nextLine().trim();

            if (!editionIssueExists(editionIssueId)) {
                System.out.println("[ERROR] Edition/Issue not found.");
                return;
            }

            String status = getEditionIssueStatus(editionIssueId);
            if (!"PUBLISHED".equals(status)) {
                System.out.println("[ERROR] Orders can only be placed for PUBLISHED editions/issues.");
                return;
            }

            Double unitPrice = getEditionIssuePrice(editionIssueId);
            if (unitPrice == null) {
                System.out.println("[ERROR] Could not fetch edition/issue price.");
                return;
            }

            System.out.print("Quantity: ");
            int quantity = Integer.parseInt(scanner.nextLine().trim());
            if (quantity <= 0) {
                System.out.println("[ERROR] Quantity must be positive.");
                return;
            }

            System.out.print("Shipping cost: ");
            double shipCost = Double.parseDouble(scanner.nextLine().trim());
            if (shipCost < 0) {
                System.out.println("[ERROR] Shipping cost cannot be negative.");
                return;
            }

            System.out.print("Order date (YYYY-MM-DD): ");
            Date orderDate = parseDate(scanner.nextLine().trim());
            if (orderDate == null) {
                return;
            }

            System.out.print("Required by date (YYYY-MM-DD): ");
            Date requiredByDate = parseDate(scanner.nextLine().trim());
            if (requiredByDate == null) {
                return;
            }

            if (requiredByDate.before(orderDate)) {
                System.out.println("[ERROR] Required by date must be on or after order date.");
                return;
            }

            String id = UUID.randomUUID().toString();
            String sql = "INSERT INTO Orders " +
                    "(id, quantity, unit_price, ship_cost, order_date, required_by_date, billed_date, total_billed_amount, distributor_id, edition_issue_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, id);
                ps.setInt(2, quantity);
                ps.setDouble(3, unitPrice);
                ps.setDouble(4, shipCost);
                ps.setDate(5, orderDate);
                ps.setDate(6, requiredByDate);
                ps.setNull(7, Types.DATE);
                ps.setNull(8, Types.DECIMAL);
                ps.setString(9, distributorId);
                ps.setString(10, editionIssueId);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("[SUCCESS] Order created.");
                    System.out.println("Order ID: " + id);
                } else {
                    System.out.println("[ERROR] Order was not created.");
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid numeric input.");
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Bill a distributor for an order and update the distributor's balance.
     */
    private void billDistributor() {
        boolean previousAutoCommit = true;

        try {
            System.out.print("Order ID: ");
            String orderId = scanner.nextLine().trim();

            if (!orderExists(orderId)) {
                System.out.println("[ERROR] Order not found.");
                return;
            }

            if (orderAlreadyBilled(orderId)) {
                System.out.println("[ERROR] Order is already billed.");
                return;
            }

            System.out.print("Billed date (YYYY-MM-DD): ");
            Date billedDate = parseDate(scanner.nextLine().trim());
            if (billedDate == null) {
                return;
            }

            Date orderDate = getOrderDate(orderId);
            if (orderDate == null) {
                System.out.println("[ERROR] Could not retrieve order date.");
                return;
            }

            if (billedDate.before(orderDate)) {
                System.out.println("[ERROR] Billed date cannot be before order date.");
                return;
            }

            OrderBillingInfo info = getOrderBillingInfo(orderId);
            if (info == null) {
                System.out.println("[ERROR] Could not retrieve order billing data.");
                return;
            }

            double totalBilledAmount = (info.quantity * info.unitPrice) + info.shipCost;

            String updateOrderSql =
                    "UPDATE Orders SET billed_date = ?, total_billed_amount = ? WHERE id = ?";
            String updateDistributorSql =
                    "UPDATE Distributors SET balance = balance + ? WHERE id = ?";

            // Preserve the current auto-commit setting because we need to manage
            // transaction boundaries manually for this billing workflow.
            previousAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try (PreparedStatement psOrder = conn.prepareStatement(updateOrderSql);
                 PreparedStatement psDistributor = conn.prepareStatement(updateDistributorSql)) {

                psOrder.setDate(1, billedDate);
                psOrder.setDouble(2, totalBilledAmount);
                psOrder.setString(3, orderId);
                psOrder.executeUpdate();

                psDistributor.setDouble(1, totalBilledAmount);
                psDistributor.setString(2, info.distributorId);
                psDistributor.executeUpdate();

                // The order billing and distributor balance update are both complete,
                // so commit them together as a single atomic transaction.
                conn.commit();
                System.out.println("[SUCCESS] Order billed and distributor balance updated.");

            } catch (SQLException e) {
                // If any step in the billing transaction fails, undo all changes.
                conn.rollback();
                System.out.println("[ERROR] " + e.getMessage());
                System.out.println("Transaction rolled back. No changes made.");
            } finally {
                // Restore the connection's original auto-commit setting after the transaction.
                conn.setAutoCommit(previousAutoCommit);
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
            tryRestoreAutoCommit(previousAutoCommit);
        }
    }

    /**
     * Record a payment received from a distributor and adjust the balance.
     */
    private void recordPayment() {
        boolean previousAutoCommit = true;

        try {
            System.out.print("Distributor ID: ");
            String distributorId = scanner.nextLine().trim();

            if (!distributorExists(distributorId)) {
                System.out.println("[ERROR] Distributor not found.");
                return;
            }

            System.out.print("Amount: ");
            double amount = Double.parseDouble(scanner.nextLine().trim());
            if (amount <= 0) {
                System.out.println("[ERROR] Payment amount must be positive.");
                return;
            }

            System.out.print("Payment date (YYYY-MM-DD): ");
            Date payDate = parseDate(scanner.nextLine().trim());
            if (payDate == null) {
                return;
            }

            String paymentId = UUID.randomUUID().toString();

            String insertPaymentSql =
                    "INSERT INTO DistPayment (id, amount, pay_date, distributor_id) VALUES (?, ?, ?, ?)";
            String updateDistributorSql =
                    "UPDATE Distributors SET balance = balance - ? WHERE id = ?";

            // Use an explicit transaction for payment recording plus balance update.
            previousAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try (PreparedStatement psPayment = conn.prepareStatement(insertPaymentSql);
                 PreparedStatement psDistributor = conn.prepareStatement(updateDistributorSql)) {

                psPayment.setString(1, paymentId);
                psPayment.setDouble(2, amount);
                psPayment.setDate(3, payDate);
                psPayment.setString(4, distributorId);
                psPayment.executeUpdate();

                psDistributor.setDouble(1, amount);
                psDistributor.setString(2, distributorId);
                psDistributor.executeUpdate();

                // Commit both the payment row and balance adjustment together.
                conn.commit();
                System.out.println("[SUCCESS] Distributor payment recorded and balance updated.");
                System.out.println("DistPayment ID: " + paymentId);

            } catch (SQLException e) {
                // The payment insert and balance update must both succeed together.
                // Roll back entire transaction if either statement fails.
                conn.rollback();
                System.out.println("[ERROR] " + e.getMessage());
                System.out.println("Transaction rolled back. No changes made.");
            } finally {
                // Ensure auto-commit is returned to its previous state after transaction logic.
                conn.setAutoCommit(previousAutoCommit);
            }

        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid numeric input.");
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
            tryRestoreAutoCommit(previousAutoCommit);
        }
    }

    /**
     * Display distributors whose billed total does not match payments made.
     */
    private void findMismatchedDistributors() {
        String sql =
                "WITH billed AS (" +
                "    SELECT o.distributor_id, COALESCE(SUM(o.total_billed_amount), 0) AS total_billed " +
                "    FROM Orders o " +
                "    GROUP BY o.distributor_id" +
                "), " +
                "paid AS (" +
                "    SELECT dp.distributor_id, COALESCE(SUM(dp.amount), 0) AS total_paid " +
                "    FROM DistPayment dp " +
                "    GROUP BY dp.distributor_id" +
                ") " +
                "SELECT d.id AS distributor_id, d.name, " +
                "       COALESCE(b.total_billed, 0) AS total_billed, " +
                "       COALESCE(p.total_paid, 0) AS total_paid " +
                "FROM Distributors d " +
                "LEFT JOIN billed b ON b.distributor_id = d.id " +
                "LEFT JOIN paid p ON p.distributor_id = d.id " +
                "WHERE COALESCE(b.total_billed, 0) <> COALESCE(p.total_paid, 0) " +
                "ORDER BY d.name";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            printResultSetAsTable(rs);
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void listDistributorsByTypeCity() {
        System.out.print("Type (BOOKSTORE/WHOLESALE/LIBRARY): ");
        String type = scanner.nextLine().trim().toUpperCase();

        System.out.print("City: ");
        String city = scanner.nextLine().trim();

        if (!isValidDistributorType(type)) {
            System.out.println("[ERROR] Invalid distributor type.");
            return;
        }

        if (city.isEmpty()) {
            System.out.println("[ERROR] City is required.");
            return;
        }

        String sql =
                "SELECT id AS distributor_id, name, address, phone_number, contact_name, balance " +
                "FROM Distributors " +
                "WHERE type = ? AND city = ? " +
                "ORDER BY name";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setString(2, city);

            try (ResultSet rs = ps.executeQuery()) {
                printResultSetAsTable(rs);
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private boolean distributorExists(String distributorId) {
        String sql = "SELECT 1 FROM Distributors WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, distributorId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean editionIssueExists(String editionIssueId) {
        String sql = "SELECT 1 FROM EditionIssue WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, editionIssueId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean orderExists(String orderId) {
        String sql = "SELECT 1 FROM Orders WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean phoneNumberExists(String phoneNumber) {
        String sql = "SELECT 1 FROM Distributors WHERE phone_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phoneNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean ordersExistForDistributor(String distributorId) {
        String sql = "SELECT 1 FROM Orders WHERE distributor_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, distributorId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return true;
        }
    }

    private boolean distPaymentsExistForDistributor(String distributorId) {
        String sql = "SELECT 1 FROM DistPayment WHERE distributor_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, distributorId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return true;
        }
    }

    private boolean orderAlreadyBilled(String orderId) {
        String sql = "SELECT billed_date FROM Orders WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDate("billed_date") != null;
                }
                return false;
            }
        } catch (SQLException e) {
            return true;
        }
    }

    private String getEditionIssueStatus(String editionIssueId) {
        String sql = "SELECT status FROM EditionIssue WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, editionIssueId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("status") : null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    private Double getEditionIssuePrice(String editionIssueId) {
        String sql = "SELECT price FROM EditionIssue WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, editionIssueId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble("price") : null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    private Date getOrderDate(String orderId) {
        String sql = "SELECT order_date FROM Orders WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDate("order_date") : null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    private OrderBillingInfo getOrderBillingInfo(String orderId) {
        String sql = "SELECT quantity, unit_price, ship_cost, distributor_id FROM Orders WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    OrderBillingInfo info = new OrderBillingInfo();
                    info.quantity = rs.getInt("quantity");
                    info.unitPrice = rs.getDouble("unit_price");
                    info.shipCost = rs.getDouble("ship_cost");
                    info.distributorId = rs.getString("distributor_id");
                    return info;
                }
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    private String getDistributorValue(String distributorId, String columnName) throws SQLException {
        String sql = "SELECT " + columnName + " FROM Distributors WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, distributorId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    private boolean isValidDistributorType(String type) {
        return "BOOKSTORE".equals(type) ||
               "WHOLESALE".equals(type) ||
               "LIBRARY".equals(type);
    }

    private Date parseDate(String text) {
        try {
            return Date.valueOf(text);
        } catch (IllegalArgumentException e) {
            System.out.println("[ERROR] Invalid date format. Use YYYY-MM-DD.");
            return null;
        }
    }

    private void tryRestoreAutoCommit(boolean previousAutoCommit) {
        try {
            conn.setAutoCommit(previousAutoCommit);
        } catch (SQLException ignored) {
        }
    }

    private String nullSafe(String value) {
        return value == null ? "NULL" : value;
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

    private static class OrderBillingInfo {
        int quantity;
        double unitPrice;
        double shipCost;
        String distributorId;
    }
}