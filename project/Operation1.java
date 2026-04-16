import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * Menu-driven class for editing and publishing workflows.
 *
 * This class handles publication creation, editor assignment,
 * and table-of-contents content management for the application.
 */
public class Operation1 {

    private final Connection conn;
    private final Scanner scanner;

    public Operation1(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    /**
     * Run the Editing and Publishing submenu until the user chooses to return.
     */
    public void runMenu() {
        boolean back = false;

        while (!back) {
            printMenu();
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice.toUpperCase()) {
                case "1A":
                case "1":
                    enterPublication();
                    break;
                case "1B":
                case "2":
                    updatePublication();
                    break;
                case "1C":
                case "3":
                    assignEditor();
                    break;
                case "1D":
                case "4":
                    removeEditor();
                    break;
                case "1E":
                case "5":
                    viewPublicationsByEditor();
                    break;
                case "1F":
                case "6":
                    addContentToTOC();
                    break;
                case "1G":
                case "7":
                    deleteContentFromTOC();
                    break;
                case "1H":
                case "8":
                    addPerson();
                    break;
                case "1I":
                case "9":
                    removePerson();
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
     * Print the available operations for Editing and Publishing.
     */
    private void printMenu() {
        System.out.println("==================================");
        System.out.println("=== Editing and Publishing =======");
        System.out.println("==================================");
        System.out.println("1A. Enter new publication");
        System.out.println("1B. Update existing publication");
        System.out.println("1C. Assign editor to publication");
        System.out.println("1D. Remove editor from publication");
        System.out.println("1E. View all publications for an editor");
        System.out.println("1F. Add article/chapter to TOC + assign author");
        System.out.println("1G. Delete article/chapter from TOC");
        System.out.println("1H. Add new person");
        System.out.println("1I. Remove person");
        System.out.println("0. Back");
    }

    /**
     * Prompt the user for publication details and insert a new publication row.
     */
    private void enterPublication() {
        String sql = "INSERT INTO Publications (id, title, type, periodicity, primary_topic) VALUES (?, ?, ?, ?, ?)";

        try {
            String id = UUID.randomUUID().toString();

            System.out.print("Title: ");
            String title = scanner.nextLine().trim();

            System.out.print("Type (BOOK/JOURNAL/MAGAZINE): ");
            String type = scanner.nextLine().trim().toUpperCase();

            System.out.print("Primary topic: ");
            String primaryTopic = scanner.nextLine().trim();

            if (title.isEmpty() || primaryTopic.isEmpty()) {
                System.out.println("[ERROR] Title and primary topic are required.");
                return;
            }

            String periodicity = null;

            if ("BOOK".equals(type)) {
                periodicity = null;
            } else if ("JOURNAL".equals(type) || "MAGAZINE".equals(type)) {
                System.out.print("Periodicity: ");
                periodicity = scanner.nextLine().trim();

                if (periodicity.isEmpty()) {
                    System.out.println("[ERROR] Periodicity is required for JOURNAL or MAGAZINE.");
                    return;
                }
            } else {
                System.out.println("[ERROR] Type must be BOOK, JOURNAL, or MAGAZINE.");
                return;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, id);
                ps.setString(2, title);
                ps.setString(3, type);

                if (periodicity == null) {
                    ps.setNull(4, java.sql.Types.VARCHAR);
                } else {
                    ps.setString(4, periodicity);
                }

                ps.setString(5, primaryTopic);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("[SUCCESS] Publication created.");
                    System.out.println("Publication ID: " + id);
                } else {
                    System.out.println("[ERROR] Publication was not created.");
                }
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Update title, periodicity, or primary topic for an existing publication.
     */
    private void updatePublication() {
        System.out.print("Publication ID: ");
        String publicationId = scanner.nextLine().trim();

        if (!publicationExists(publicationId)) {
            System.out.println("[ERROR] Publication not found.");
            return;
        }

        try {
            String currentType = getPublicationType(publicationId);
            String currentTitle = getPublicationTitle(publicationId);
            String currentPeriodicity = getPublicationPeriodicity(publicationId);
            String currentPrimaryTopic = getPublicationPrimaryTopic(publicationId);

            System.out.println("Leave any field blank to keep current value.");
            System.out.println("Publication type cannot be changed.");

            System.out.print("New title [" + nullSafe(currentTitle) + "]: ");
            String newTitle = scanner.nextLine().trim();

            String newPeriodicity = "";
            if ("JOURNAL".equals(currentType) || "MAGAZINE".equals(currentType)) {
                System.out.print("New periodicity [" + nullSafe(currentPeriodicity) + "]: ");
                newPeriodicity = scanner.nextLine().trim();
            }

            System.out.print("New primary topic [" + nullSafe(currentPrimaryTopic) + "]: ");
            String newPrimaryTopic = scanner.nextLine().trim();

            String finalTitle = newTitle.isEmpty() ? currentTitle : newTitle;
            String finalPrimaryTopic = newPrimaryTopic.isEmpty() ? currentPrimaryTopic : newPrimaryTopic;

            String finalPeriodicity;
            if ("BOOK".equals(currentType)) {
                finalPeriodicity = null;
            } else {
                finalPeriodicity = newPeriodicity.isEmpty() ? currentPeriodicity : newPeriodicity;
                if (finalPeriodicity == null || finalPeriodicity.trim().isEmpty()) {
                    System.out.println("[ERROR] Periodicity is required for JOURNAL or MAGAZINE.");
                    return;
                }
            }

            if (finalTitle == null || finalTitle.trim().isEmpty() ||
                finalPrimaryTopic == null || finalPrimaryTopic.trim().isEmpty()) {
                System.out.println("[ERROR] Title and primary topic cannot be empty.");
                return;
            }

            String sql = "UPDATE Publications SET title = ?, periodicity = ?, primary_topic = ? WHERE id = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, finalTitle);

                if (finalPeriodicity == null) {
                    ps.setNull(2, java.sql.Types.VARCHAR);
                } else {
                    ps.setString(2, finalPeriodicity);
                }

                ps.setString(3, finalPrimaryTopic);
                ps.setString(4, publicationId);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("[SUCCESS] Publication updated.");
                } else {
                    System.out.println("[ERROR] No publication updated.");
                }
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Assign an editor to a publication when the person has the correct role.
     */
    private void assignEditor() {
        System.out.print("Publication ID: ");
        String publicationId = scanner.nextLine().trim();

        System.out.print("Person ID: ");
        String personId = scanner.nextLine().trim();

        if (!publicationExists(publicationId)) {
            System.out.println("[ERROR] Publication not found.");
            return;
        }

        if (!personExists(personId)) {
            System.out.println("[ERROR] Person not found.");
            return;
        }

        String role = getPersonRole(personId);
        if (!"EDITOR".equals(role) && !"BOTH".equals(role)) {
            System.out.println("[ERROR] Person must have role EDITOR or BOTH.");
            return;
        }

        if (editAssignmentExists(personId, publicationId)) {
            System.out.println("[ERROR] Editor is already assigned to this publication.");
            return;
        }

        String sql = "INSERT INTO Edits (person_id, publication_id) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, personId);
            ps.setString(2, publicationId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("[SUCCESS] Editor assigned to publication.");
            } else {
                System.out.println("[ERROR] Assignment failed.");
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Remove an editor assignment from a publication.
     */
    private void removeEditor() {
        System.out.print("Publication ID: ");
        String publicationId = scanner.nextLine().trim();

        System.out.print("Person ID: ");
        String personId = scanner.nextLine().trim();

        if (!editAssignmentExists(personId, publicationId)) {
            System.out.println("[ERROR] That editor-publication assignment does not exist.");
            return;
        }

        String sql = "DELETE FROM Edits WHERE person_id = ? AND publication_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, personId);
            ps.setString(2, publicationId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("[SUCCESS] Editor removed from publication.");
            } else {
                System.out.println("[ERROR] No assignment removed.");
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Display all publications assigned to the specified editor.
     */
    private void viewPublicationsByEditor() {
        System.out.print("Editor Person ID: ");
        String personId = scanner.nextLine().trim();

        if (!personExists(personId)) {
            System.out.println("[ERROR] Person not found.");
            return;
        }

        String role = getPersonRole(personId);
        if (!"EDITOR".equals(role) && !"BOTH".equals(role)) {
            System.out.println("[ERROR] Person is not an editor.");
            return;
        }

        String sql = "SELECT e.person_id, p.id AS publication_id, p.title, p.type, p.periodicity, p.primary_topic " +
                     "FROM Publications p " +
                     "JOIN Edits e ON p.id = e.publication_id " +
                     "WHERE e.person_id = ? " +
                     "ORDER BY p.title";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, personId);

            try (ResultSet rs = ps.executeQuery()) {
                printResultSetAsTable(rs);
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Add new content to the table of contents and link it with its author.
     */
    private void addContentToTOC() {
        boolean previousAutoCommit = true;

        try {
            System.out.print("Edition/Issue ID: ");
            String editionIssueId = scanner.nextLine().trim();

            if (!editionIssueExists(editionIssueId)) {
                System.out.println("[ERROR] Edition/Issue not found.");
                return;
            }

            String publicationType = getPublicationTypeFromEditionIssue(editionIssueId);
            String publicationTopic = getPublicationTopicFromEditionIssue(editionIssueId);

            System.out.println("Parent publication type: " + publicationType);
            System.out.println("Parent publication topic: " + publicationTopic);

            System.out.print("Content title: ");
            String contentTitle = scanner.nextLine().trim();

            System.out.print("Content topic: ");
            String topic = scanner.nextLine().trim();

            System.out.print("Date written (YYYY-MM-DD): ");
            String dateWrittenInput = scanner.nextLine().trim();

            System.out.print("Content type (ARTICLE/CHAPTER/SECTION): ");
            String contentType = scanner.nextLine().trim().toUpperCase();

            System.out.print("Content text: ");
            String contentText = scanner.nextLine().trim();

            System.out.print("Author Person ID: ");
            String personId = scanner.nextLine().trim();

            if (contentTitle.isEmpty() || topic.isEmpty() || contentText.isEmpty()) {
                System.out.println("[ERROR] Content title, topic, and text are required.");
                return;
            }

            if (!personExists(personId)) {
                System.out.println("[ERROR] Author not found.");
                return;
            }

            String role = getPersonRole(personId);
            if (!"AUTHOR".equals(role) && !"BOTH".equals(role)) {
                System.out.println("[ERROR] Person must have role AUTHOR or BOTH.");
                return;
            }

            if ("BOOK".equals(publicationType)) {
                if (!"CHAPTER".equals(contentType) && !"SECTION".equals(contentType)) {
                    System.out.println("[ERROR] Books can only contain CHAPTER or SECTION.");
                    return;
                }
            } else if ("JOURNAL".equals(publicationType) || "MAGAZINE".equals(publicationType)) {
                if (!"ARTICLE".equals(contentType)) {
                    System.out.println("[ERROR] Periodic publications can only contain ARTICLE.");
                    return;
                }
            } else {
                System.out.println("[ERROR] Unsupported publication type.");
                return;
            }

            if (!topic.equalsIgnoreCase(publicationTopic)) {
                System.out.println("Publication topic: " + publicationTopic);
                System.out.println("Entered content topic: " + topic);
                System.out.println("[WARNING] Content topic differs from publication primary topic.");
            }

            Date dateWritten;
            try {
                dateWritten = Date.valueOf(dateWrittenInput);
            } catch (IllegalArgumentException e) {
                System.out.println("[ERROR] Invalid date format. Use YYYY-MM-DD.");
                return;
            }

            String contentId = UUID.randomUUID().toString();

            String insertContentSql =
                "INSERT INTO Content (id, content_title, topic, date_written, content_type, content_text, edition_issue_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

            String insertWritesSql =
                "INSERT INTO Writes (person_id, content_id) VALUES (?, ?)";

            previousAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try (PreparedStatement psContent = conn.prepareStatement(insertContentSql);
                 PreparedStatement psWrites = conn.prepareStatement(insertWritesSql)) {

                psContent.setString(1, contentId);
                psContent.setString(2, contentTitle);
                psContent.setString(3, topic);
                psContent.setDate(4, dateWritten);
                psContent.setString(5, contentType);
                psContent.setString(6, contentText);
                psContent.setString(7, editionIssueId);
                psContent.executeUpdate();

                psWrites.setString(1, personId);
                psWrites.setString(2, contentId);
                psWrites.executeUpdate();

                conn.commit();
                System.out.println("[SUCCESS] Content added to TOC and author assigned.");
                System.out.println("Content ID: " + contentId);

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("[ERROR] " + e.getMessage());
                System.out.println("Transaction rolled back. No changes made.");
            } finally {
                conn.setAutoCommit(previousAutoCommit);
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
            tryRestoreAutoCommit(previousAutoCommit);
        }
    }

    /**
     * Delete content and its associated writes relationship from the TOC.
     */
    private void deleteContentFromTOC() {
        boolean previousAutoCommit = true;

        try {
            System.out.print("Content ID: ");
            String contentId = scanner.nextLine().trim();

            if (!contentExists(contentId)) {
                System.out.println("[ERROR] Content not found.");
                return;
            }

            String deleteWritesSql = "DELETE FROM Writes WHERE content_id = ?";
            String deleteContentSql = "DELETE FROM Content WHERE id = ?";

            previousAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try (PreparedStatement psWrites = conn.prepareStatement(deleteWritesSql);
                 PreparedStatement psContent = conn.prepareStatement(deleteContentSql)) {

                psWrites.setString(1, contentId);
                psWrites.executeUpdate();

                psContent.setString(1, contentId);
                int rows = psContent.executeUpdate();

                if (rows == 0) {
                    throw new SQLException("Content delete failed.");
                }

                conn.commit();
                System.out.println("[SUCCESS] Content removed from TOC.");

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("[ERROR] " + e.getMessage());
                System.out.println("Transaction rolled back. No changes made.");
            } finally {
                conn.setAutoCommit(previousAutoCommit);
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
            tryRestoreAutoCommit(previousAutoCommit);
        }
    }

    /**
     * Prompt the user for person details and insert a new person row.
     */
    private void addPerson() {
        String sql = "INSERT INTO Person (id, name, role, affiliation) VALUES (?, ?, ?, ?)";

        try {
            String id = UUID.randomUUID().toString();

            System.out.print("Name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Role (AUTHOR / EDITOR / BOTH): ");
            String role = scanner.nextLine().trim().toUpperCase();

            System.out.print("Affiliation (STAFF / INVITED): ");
            String affiliation = scanner.nextLine().trim().toUpperCase();

            if (name.isEmpty()) {
                System.out.println("[ERROR] Name is required.");
                return;
            }

            if (!"AUTHOR".equals(role) && !"EDITOR".equals(role) && !"BOTH".equals(role)) {
                System.out.println("[ERROR] Role must be one of: AUTHOR, EDITOR, BOTH");
                return;
            }

            if (!"STAFF".equals(affiliation) && !"INVITED".equals(affiliation)) {
                System.out.println("[ERROR] Affiliation must be one of: STAFF, INVITED");
                return;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, id);
                ps.setString(2, name);
                ps.setString(3, role);
                ps.setString(4, affiliation);

                ps.executeUpdate();
                System.out.println("Person added successfully. ID: " + id);
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Delete a person when there are no dependent rows in related tables.
     */
    private void removePerson() {
        System.out.print("Person ID: ");
        String personId = scanner.nextLine().trim();

        if (!personExists(personId)) {
            System.out.println("[ERROR] Person not found.");
            return;
        }

        if (editAssignmentsExistForPerson(personId)) {
            System.out.println("[ERROR] Cannot delete person because related Edits rows exist.");
            return;
        }

        if (writesExistForPerson(personId)) {
            System.out.println("[ERROR] Cannot delete person because related Writes rows exist.");
            return;
        }

        if (paymentsExistForPerson(personId)) {
            System.out.println("[ERROR] Cannot delete person because related UserPayments rows exist.");
            return;
        }

        String sql = "DELETE FROM Person WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, personId);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Person removed successfully.");
            } else {
                System.out.println("[ERROR] No person deleted.");
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private boolean publicationExists(String publicationId) {
        String sql = "SELECT 1 FROM Publications WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, publicationId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean personExists(String personId) {
        String sql = "SELECT 1 FROM Person WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, personId);
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

    private boolean contentExists(String contentId) {
        String sql = "SELECT 1 FROM Content WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, contentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean editAssignmentExists(String personId, String publicationId) {
        String sql = "SELECT 1 FROM Edits WHERE person_id = ? AND publication_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, personId);
            ps.setString(2, publicationId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean editAssignmentsExistForPerson(String personId) {
        String sql = "SELECT 1 FROM Edits WHERE person_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, personId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return true;
        }
    }

    private boolean writesExistForPerson(String personId) {
        String sql = "SELECT 1 FROM Writes WHERE person_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, personId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return true;
        }
    }

    private boolean paymentsExistForPerson(String personId) {
        String sql = "SELECT 1 FROM UserPayments WHERE person_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, personId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return true;
        }
    }

    private String getPersonRole(String personId) {
        String sql = "SELECT role FROM Person WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, personId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    private String getPublicationType(String publicationId) {
        String sql = "SELECT type FROM Publications WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, publicationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("type");
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    private String getPublicationTitle(String publicationId) {
        String sql = "SELECT title FROM Publications WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, publicationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("title");
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    private String getPublicationPeriodicity(String publicationId) {
        String sql = "SELECT periodicity FROM Publications WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, publicationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("periodicity");
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    private String getPublicationPrimaryTopic(String publicationId) {
        String sql = "SELECT primary_topic FROM Publications WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, publicationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("primary_topic");
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    private String getPublicationTypeFromEditionIssue(String editionIssueId) {
        String sql = "SELECT p.type " +
                     "FROM EditionIssue ei " +
                     "JOIN Publications p ON ei.publication_id = p.id " +
                     "WHERE ei.id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, editionIssueId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    private String getPublicationTopicFromEditionIssue(String editionIssueId) {
        String sql = "SELECT p.primary_topic " +
                     "FROM EditionIssue ei " +
                     "JOIN Publications p ON ei.publication_id = p.id " +
                     "WHERE ei.id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, editionIssueId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
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
}
