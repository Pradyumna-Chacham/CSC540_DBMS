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
 * Menu-driven class for production and edition/issue management.
 *
 * This class handles book editions, journal/magazine issues, author content,
 * payments, and reporting queries in the production workflow.
 */
public class Operation2 {

    private final Connection conn;
    private final Scanner scanner;

    public Operation2(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    /**
     * Run the Production of Editions/Issues submenu until the user returns.
     */
    public void runMenu() {
        boolean back = false;

        while (!back) {
            printMenu();
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "2A":
                case "1":
                    enterBookEdition();
                    break;
                case "2B":
                case "2":
                    enterIssue();
                    break;
                case "2C":
                case "3":
                    updateEditionIssue();
                    break;
                case "2D":
                case "4":
                    deleteEditionIssue();
                    break;
                case "2E":
                case "5":
                    enterContent();
                    break;
                case "2F":
                case "6":
                    updateContentMetadata();
                    break;
                case "2G":
                case "7":
                    updateContentText();
                    break;
                case "2H":
                case "8":
                    findByTopic();
                    break;
                case "2I":
                case "9":
                    findByDateRange();
                    break;
                case "2J":
                case "10":
                    findByAuthor();
                    break;
                case "2K":
                case "11":
                    enterPayment();
                    break;
                case "2L":
                case "12":
                    updateClaimedDate();
                    break;
                case "2M":
                case "13":
                    listUnclaimedPayments();
                    break;
                case "2N":
                case "14":
                    compareIssues();
                    break;
                case "2O":
                case "15":
                    viewRelatedWorkForPerson();
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
     * Print the menu options for production operations.
     */
    private void printMenu() {
        System.out.println("==========================================");
        System.out.println("=== Production of Editions / Issues ======");
        System.out.println("==========================================");
        System.out.println("2A. Enter new book edition");
        System.out.println("2B. Enter new issue of a periodic publication");
        System.out.println("2C. Update edition/issue");
        System.out.println("2D. Delete edition/issue");
        System.out.println("2E. Enter new article/chapter with author");
        System.out.println("2F. Update article/chapter metadata");
        System.out.println("2G. Update article text");
        System.out.println("2H. Find books/articles by topic");
        System.out.println("2I. Find books/articles by date range");
        System.out.println("2J. Find books/articles by author name");
        System.out.println("2K. Enter payment for author/editor");
        System.out.println("2L. Update payment claimed date");
        System.out.println("2M. List unclaimed payments in date range");
        System.out.println("2N. Compare two issues by articles");
        System.out.println("2O. View related work for a person");
        System.out.println("0. Back");
    }

    /**
     * Capture details and insert a new book edition record.
     */
    private void enterBookEdition() {
        String publicationId;
        int editionNum;
        String isbn;
        Date pubDate;
        String status;
        double price;

        try {
            System.out.print("Publication ID: ");
            publicationId = scanner.nextLine().trim();

            if (!publicationExists(publicationId)) {
                System.out.println("[ERROR] Publication not found.");
                return;
            }

            String pubType = getPublicationType(publicationId);
            if (!"BOOK".equals(pubType)) {
                System.out.println("[ERROR] Publication must be of type BOOK.");
                return;
            }

            System.out.print("Edition number: ");
            String editionInput = scanner.nextLine().trim();
            if (editionInput.isEmpty()) {
                System.out.println("[ERROR] Edition number is required for a book edition.");
                return;
            }
            editionNum = Integer.parseInt(editionInput);
            if (editionNum <= 0) {
                System.out.println("[ERROR] Edition number must be positive.");
                return;
            }

            System.out.print("ISBN: ");
            isbn = scanner.nextLine().trim();
            if (isbn.isEmpty()) {
                System.out.println("[ERROR] ISBN is required for a book edition.");
                return;
            }

            if (isbnExists(isbn)) {
                System.out.println("[ERROR] ISBN already exists.");
                return;
            }

            System.out.print("Publication date (YYYY-MM-DD), blank allowed: ");
            String pubDateInput = scanner.nextLine().trim();
            pubDate = pubDateInput.isEmpty() ? null : parseDate(pubDateInput);
            if (!pubDateInput.isEmpty() && pubDate == null) {
                return;
            }

            System.out.print("Status (IN_PROGRESS/FINISHED/PUBLISHED/UNPUBLISHED): ");
            status = scanner.nextLine().trim().toUpperCase();
            if (!isValidEditionStatus(status)) {
                System.out.println("[ERROR] Invalid status.");
                return;
            }

            System.out.print("Price: ");
            String priceInput = scanner.nextLine().trim();
            price = Double.parseDouble(priceInput);
            if (price < 0) {
                System.out.println("[ERROR] Price cannot be negative.");
                return;
            }

            String id = UUID.randomUUID().toString();
            String sql = "INSERT INTO EditionIssue (id, edition_num, pub_date, status, isbn, price, publication_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, id);
                ps.setInt(2, editionNum);
                if (pubDate == null) {
                    ps.setNull(3, Types.DATE);
                } else {
                    ps.setDate(3, pubDate);
                }
                ps.setString(4, status);
                ps.setString(5, isbn);
                ps.setDouble(6, price);
                ps.setString(7, publicationId);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("[SUCCESS] Book edition created.");
                    System.out.println("EditionIssue ID: " + id);
                } else {
                    System.out.println("[ERROR] Book edition was not created.");
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid numeric input.");
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Capture details and insert a new journal or magazine issue record.
     */
    private void enterIssue() {
        String publicationId;
        Date pubDate;
        String status;
        double price;

        try {
            System.out.print("Publication ID: ");
            publicationId = scanner.nextLine().trim();

            if (!publicationExists(publicationId)) {
                System.out.println("[ERROR] Publication not found.");
                return;
            }

            String pubType = getPublicationType(publicationId);
            if (!"JOURNAL".equals(pubType) && !"MAGAZINE".equals(pubType)) {
                System.out.println("[ERROR] Publication must be a JOURNAL or MAGAZINE.");
                return;
            }

            System.out.print("Publication date (YYYY-MM-DD), blank allowed: ");
            String pubDateInput = scanner.nextLine().trim();
            pubDate = pubDateInput.isEmpty() ? null : parseDate(pubDateInput);
            if (!pubDateInput.isEmpty() && pubDate == null) {
                return;
            }

            System.out.print("Status (IN_PROGRESS/FINISHED/PUBLISHED/UNPUBLISHED): ");
            status = scanner.nextLine().trim().toUpperCase();
            if (!isValidEditionStatus(status)) {
                System.out.println("[ERROR] Invalid status.");
                return;
            }

            System.out.print("Price: ");
            String priceInput = scanner.nextLine().trim();
            price = Double.parseDouble(priceInput);
            if (price < 0) {
                System.out.println("[ERROR] Price cannot be negative.");
                return;
            }

            String id = UUID.randomUUID().toString();
            String sql = "INSERT INTO EditionIssue (id, edition_num, pub_date, status, isbn, price, publication_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, id);
                ps.setNull(2, Types.INTEGER);
                if (pubDate == null) {
                    ps.setNull(3, Types.DATE);
                } else {
                    ps.setDate(3, pubDate);
                }
                ps.setString(4, status);
                ps.setNull(5, Types.VARCHAR);
                ps.setDouble(6, price);
                ps.setString(7, publicationId);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("[SUCCESS] Issue created.");
                    System.out.println("EditionIssue ID: " + id);
                } else {
                    System.out.println("[ERROR] Issue was not created.");
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid numeric input.");
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Update the status, price, or publication date of an edition/issue.
     */
    private void updateEditionIssue() {
        System.out.print("Edition/Issue ID: ");
        String editionIssueId = scanner.nextLine().trim();

        if (!editionIssueExists(editionIssueId)) {
            System.out.println("[ERROR] Edition/Issue not found.");
            return;
        }

        try {
            String currentStatus = getEditionIssueStatus(editionIssueId);
            Double currentPrice = getEditionIssuePrice(editionIssueId);
            Integer currentEditionNum = getEditionIssueEditionNum(editionIssueId);
            String currentIsbn = getEditionIssueIsbn(editionIssueId);

            System.out.println("Leave blank to keep current value.");
            System.out.print("New publication date (YYYY-MM-DD), blank keeps current: ");
            String newPubDateInput = scanner.nextLine().trim();

            System.out.print("New status (IN_PROGRESS/FINISHED/PUBLISHED/UNPUBLISHED) [" + nullSafe(currentStatus) + "]: ");
            String newStatus = scanner.nextLine().trim().toUpperCase();

            System.out.print("New price [" + currentPrice + "]: ");
            String newPriceInput = scanner.nextLine().trim();

            Date finalPubDate = null;
            boolean setPubDateNull = false;
            if (newPubDateInput.isEmpty()) {
                // keep current
            } else if ("NULL".equalsIgnoreCase(newPubDateInput)) {
                setPubDateNull = true;
            } else {
                finalPubDate = parseDate(newPubDateInput);
                if (finalPubDate == null) {
                    return;
                }
            }

            String finalStatus = newStatus.isEmpty() ? currentStatus : newStatus;
            if (!isValidEditionStatus(finalStatus)) {
                System.out.println("[ERROR] Invalid status.");
                return;
            }

            double finalPrice = newPriceInput.isEmpty() ? currentPrice : Double.parseDouble(newPriceInput);
            if (finalPrice < 0) {
                System.out.println("[ERROR] Price cannot be negative.");
                return;
            }

            String publicationType = getPublicationTypeFromEditionIssue(editionIssueId);

            if (("JOURNAL".equals(publicationType) || "MAGAZINE".equals(publicationType))
                    && (currentEditionNum != null || currentIsbn != null)) {
                System.out.println("[ERROR] Periodic issues must not have edition number or ISBN.");
                return;
            }

            String sql;
            if (setPubDateNull) {
                sql = "UPDATE EditionIssue SET pub_date = NULL, status = ?, price = ? WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, finalStatus);
                    ps.setDouble(2, finalPrice);
                    ps.setString(3, editionIssueId);
                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        System.out.println("[SUCCESS] Edition/Issue updated.");
                    } else {
                        System.out.println("[ERROR] No edition/issue updated.");
                    }
                }
            } else if (finalPubDate != null) {
                sql = "UPDATE EditionIssue SET pub_date = ?, status = ?, price = ? WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setDate(1, finalPubDate);
                    ps.setString(2, finalStatus);
                    ps.setDouble(3, finalPrice);
                    ps.setString(4, editionIssueId);
                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        System.out.println("[SUCCESS] Edition/Issue updated.");
                    } else {
                        System.out.println("[ERROR] No edition/issue updated.");
                    }
                }
            } else {
                sql = "UPDATE EditionIssue SET status = ?, price = ? WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, finalStatus);
                    ps.setDouble(2, finalPrice);
                    ps.setString(3, editionIssueId);
                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        System.out.println("[SUCCESS] Edition/Issue updated.");
                    } else {
                        System.out.println("[ERROR] No edition/issue updated.");
                    }
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid numeric input.");
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Delete an edition or issue if there are no related content or order rows.
     */
    private void deleteEditionIssue() {
        System.out.print("Edition/Issue ID: ");
        String editionIssueId = scanner.nextLine().trim();

        if (!editionIssueExists(editionIssueId)) {
            System.out.println("[ERROR] Edition/Issue not found.");
            return;
        }

        if (contentExistsForEditionIssue(editionIssueId)) {
            System.out.println("[ERROR] Cannot delete edition/issue because related Content rows exist.");
            return;
        }

        if (ordersExistForEditionIssue(editionIssueId)) {
            System.out.println("[ERROR] Cannot delete edition/issue because related Orders rows exist.");
            return;
        }

        String sql = "DELETE FROM EditionIssue WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, editionIssueId);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("[SUCCESS] Edition/Issue deleted.");
            } else {
                System.out.println("[ERROR] No edition/issue deleted.");
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Enter a new article/chapter and link it to an author for an edition/issue.
     */
    private void enterContent() {
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

            System.out.print("Author Person ID: ");
            String personId = scanner.nextLine().trim();

            if (!personExists(personId)) {
                System.out.println("[ERROR] Person not found.");
                return;
            }

            String role = getPersonRole(personId);
            if (!"AUTHOR".equals(role) && !"BOTH".equals(role)) {
                System.out.println("[ERROR] Person must have role AUTHOR or BOTH.");
                return;
            }

            System.out.print("Content title: ");
            String contentTitle = scanner.nextLine().trim();

            System.out.print("Topic: ");
            String topic = scanner.nextLine().trim();

            System.out.print("Date written (YYYY-MM-DD): ");
            String dateInput = scanner.nextLine().trim();
            Date dateWritten = parseDate(dateInput);
            if (dateWritten == null) {
                return;
            }

            System.out.print("Content type (ARTICLE/CHAPTER/SECTION): ");
            String contentType = scanner.nextLine().trim().toUpperCase();

            System.out.print("Content text: ");
            String contentText = scanner.nextLine().trim();

            if (contentTitle.isEmpty() || topic.isEmpty() || contentText.isEmpty()) {
                System.out.println("[ERROR] Title, topic, and text are required.");
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
                System.out.println("[ERROR] Content topic must match the publication primary topic.");
                return;
            }

            String contentId = UUID.randomUUID().toString();

            String insertContentSql =
                    "INSERT INTO Content (id, content_title, topic, date_written, content_type, content_text, edition_issue_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            String insertWritesSql =
                    "INSERT INTO Writes (person_id, content_id) VALUES (?, ?)";

            // Save the current auto-commit state so it can be restored later.
            // Disable auto-commit because this operation involves multiple related
            // inserts that must either both succeed or both fail together.
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

                // Both related inserts succeeded, commit the transaction so data is saved atomically.
                conn.commit();
                System.out.println("[SUCCESS] Content created and author linked.");
                System.out.println("Content ID: " + contentId);

            } catch (SQLException e) {
                // On any SQL failure, rollback the transaction to avoid partial writes.
                conn.rollback();
                System.out.println("[ERROR] " + e.getMessage());
                System.out.println("Transaction rolled back. No changes made.");
            } finally {
                // Restore the original auto-commit mode after the transaction completes.
                conn.setAutoCommit(previousAutoCommit);
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
            tryRestoreAutoCommit(previousAutoCommit);
        }
    }

    /**
     * Update metadata fields for existing content.
     */
    private void updateContentMetadata() {
        System.out.print("Content ID: ");
        String contentId = scanner.nextLine().trim();

        if (!contentExists(contentId)) {
            System.out.println("[ERROR] Content not found.");
            return;
        }

        try {
            String currentTitle = getContentTitle(contentId);
            String currentTopic = getContentTopic(contentId);
            Date currentDate = getContentDateWritten(contentId);

            System.out.println("Leave blank to keep current value.");

            System.out.print("New content title [" + nullSafe(currentTitle) + "]: ");
            String newTitle = scanner.nextLine().trim();

            System.out.print("New topic [" + nullSafe(currentTopic) + "]: ");
            String newTopic = scanner.nextLine().trim();

            System.out.print("New date written (YYYY-MM-DD) [" + currentDate + "]: ");
            String newDateInput = scanner.nextLine().trim();

            String finalTitle = newTitle.isEmpty() ? currentTitle : newTitle;
            String finalTopic = newTopic.isEmpty() ? currentTopic : newTopic;

            Date finalDate;
            if (newDateInput.isEmpty()) {
                finalDate = currentDate;
            } else {
                finalDate = parseDate(newDateInput);
                if (finalDate == null) {
                    return;
                }
            }

            if (finalTitle == null || finalTitle.trim().isEmpty() ||
                finalTopic == null || finalTopic.trim().isEmpty() ||
                finalDate == null) {
                System.out.println("[ERROR] Title, topic, and date_written cannot be empty.");
                return;
            }

            String editionIssueId = getEditionIssueIdForContent(contentId);
            String publicationTopic = getPublicationTopicFromEditionIssue(editionIssueId);

            if (!finalTopic.equalsIgnoreCase(publicationTopic)) {
                System.out.println("[ERROR] Content topic must match the publication primary topic.");
                return;
            }

            String sql = "UPDATE Content SET content_title = ?, topic = ?, date_written = ? WHERE id = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, finalTitle);
                ps.setString(2, finalTopic);
                ps.setDate(3, finalDate);
                ps.setString(4, contentId);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("[SUCCESS] Content metadata updated.");
                } else {
                    System.out.println("[ERROR] No content updated.");
                }
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Update the text body for an existing content item.
     */
    private void updateContentText() {
        System.out.print("Content ID: ");
        String contentId = scanner.nextLine().trim();

        if (!contentExists(contentId)) {
            System.out.println("[ERROR] Content not found.");
            return;
        }

        System.out.print("New content text: ");
        String contentText = scanner.nextLine().trim();

        if (contentText.isEmpty()) {
            System.out.println("[ERROR] Content text cannot be empty.");
            return;
        }

        String sql = "UPDATE Content SET content_text = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, contentText);
            ps.setString(2, contentId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("[SUCCESS] Content text updated.");
            } else {
                System.out.println("[ERROR] No content text updated.");
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void findByTopic() {
        System.out.print("Topic: ");
        String topic = scanner.nextLine().trim();

        if (topic.isEmpty()) {
            System.out.println("[ERROR] Topic is required.");
            return;
        }

        String sql =
                "SELECT 'Book' AS result_type, id AS entity_id, title AS name, primary_topic AS topic " +
                "FROM Publications WHERE primary_topic = ? AND type = 'BOOK' " +
                "UNION " +
                "SELECT 'Article' AS result_type, id AS entity_id, content_title AS name, topic " +
                "FROM Content WHERE topic = ? AND content_type = 'ARTICLE'";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, topic);
            ps.setString(2, topic);

            try (ResultSet rs = ps.executeQuery()) {
                printResultSetAsTable(rs);
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void findByDateRange() {
        System.out.print("Start date (YYYY-MM-DD): ");
        String startInput = scanner.nextLine().trim();
        Date startDate = parseDate(startInput);
        if (startDate == null) {
            return;
        }

        System.out.print("End date (YYYY-MM-DD): ");
        String endInput = scanner.nextLine().trim();
        Date endDate = parseDate(endInput);
        if (endDate == null) {
            return;
        }

        if (endDate.before(startDate)) {
            System.out.println("[ERROR] End date must be on or after start date.");
            return;
        }

        String sql =
                "SELECT 'Book' AS result_type, pub.id AS entity_id, pub.title AS title, MIN(ei.pub_date) AS relevant_date, pub.id AS parent_id " +
                "FROM Publications pub " +
                "JOIN EditionIssue ei ON ei.publication_id = pub.id " +
                "WHERE pub.type = 'BOOK' AND ei.pub_date BETWEEN ? AND ? " +
                "GROUP BY pub.id, pub.title " +
                "UNION " +
                "SELECT 'Article' AS result_type, c.id AS entity_id, c.content_title AS title, c.date_written AS relevant_date, c.edition_issue_id AS parent_id " +
                "FROM Content c " +
                "WHERE c.date_written BETWEEN ? AND ? AND c.content_type = 'ARTICLE'";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, startDate);
            ps.setDate(2, endDate);
            ps.setDate(3, startDate);
            ps.setDate(4, endDate);

            try (ResultSet rs = ps.executeQuery()) {
                printResultSetAsTable(rs);
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void findByAuthor() {
        System.out.print("Author name: ");
        String authorName = scanner.nextLine().trim();

        if (authorName.isEmpty()) {
            System.out.println("[ERROR] Author name is required.");
            return;
        }

        String booksSql =
                "SELECT p.name AS author_name, pub.title AS publication_title, pub.id AS publication_id " +
                "FROM Writes w " +
                "JOIN Person p ON w.person_id = p.id " +
                "JOIN Content c ON c.id = w.content_id " +
                "JOIN EditionIssue ei ON ei.id = c.edition_issue_id " +
                "JOIN Publications pub ON pub.id = ei.publication_id " +
                "WHERE p.name = ? AND c.content_type = 'CHAPTER' AND pub.type = 'BOOK' " +
                "GROUP BY p.name, pub.title, pub.id";

        String articlesSql =
                "SELECT p.name AS author_name, c.id AS content_id, c.content_title AS title, c.topic, c.date_written " +
                "FROM Writes w " +
                "JOIN Person p ON p.id = w.person_id " +
                "JOIN Content c ON c.id = w.content_id " +
                "WHERE p.name = ? AND c.content_type = 'ARTICLE'";

        try (PreparedStatement psBooks = conn.prepareStatement(booksSql);
             PreparedStatement psArticles = conn.prepareStatement(articlesSql)) {

            psBooks.setString(1, authorName);
            psArticles.setString(1, authorName);

            System.out.println("Books by author:");
            try (ResultSet rsBooks = psBooks.executeQuery()) {
                printResultSetAsTable(rsBooks);
            }

            System.out.println();
            System.out.println("Articles by author:");
            try (ResultSet rsArticles = psArticles.executeQuery()) {
                printResultSetAsTable(rsArticles);
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void enterPayment() {
        try {
            System.out.print("Person ID: ");
            String personId = scanner.nextLine().trim();

            if (!personExists(personId)) {
                System.out.println("[ERROR] Person not found.");
                return;
            }

            System.out.print("Payment type (BOOK_AUTHORSHIP/ARTICLE_AUTHORSHIP/EDITORIAL_WORK): ");
            String paymentType = scanner.nextLine().trim().toUpperCase();
            if (!isValidPaymentType(paymentType)) {
                System.out.println("[ERROR] Invalid payment type.");
                return;
            }

            System.out.print("Amount: ");
            double amount = Double.parseDouble(scanner.nextLine().trim());
            if (amount < 0) {
                System.out.println("[ERROR] Amount cannot be negative.");
                return;
            }

            System.out.print("Issue date (YYYY-MM-DD): ");
            Date issueDate = parseDate(scanner.nextLine().trim());
            if (issueDate == null) {
                return;
            }

            System.out.print("Edition/Issue ID, blank allowed: ");
            String editionIssueId = scanner.nextLine().trim();
            if (!editionIssueId.isEmpty() && !editionIssueExists(editionIssueId)) {
                System.out.println("[ERROR] Edition/Issue not found.");
                return;
            }

            System.out.print("Claimed date (YYYY-MM-DD), blank allowed: ");
            String claimedInput = scanner.nextLine().trim();
            Date claimedDate = null;
            if (!claimedInput.isEmpty()) {
                claimedDate = parseDate(claimedInput);
                if (claimedDate == null) {
                    return;
                }
                if (claimedDate.before(issueDate)) {
                    System.out.println("[ERROR] Claimed date cannot be before issue date.");
                    return;
                }
            }

            String id = UUID.randomUUID().toString();
            String sql = "INSERT INTO UserPayments (id, payment_type, amount, issue_date, claimed_date, person_id, edition_issue_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, id);
                ps.setString(2, paymentType);
                ps.setDouble(3, amount);
                ps.setDate(4, issueDate);
                if (claimedDate == null) {
                    ps.setNull(5, Types.DATE);
                } else {
                    ps.setDate(5, claimedDate);
                }
                ps.setString(6, personId);
                if (editionIssueId.isEmpty()) {
                    ps.setNull(7, Types.CHAR);
                } else {
                    ps.setString(7, editionIssueId);
                }

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("[SUCCESS] Payment created.");
                    System.out.println("UserPayment ID: " + id);
                } else {
                    System.out.println("[ERROR] Payment was not created.");
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid numeric input.");
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

        private void updateClaimedDate() {
        System.out.print("UserPayment ID: ");
        String paymentId = scanner.nextLine().trim();

        if (!paymentExists(paymentId)) {
            System.out.println("[ERROR] Payment not found.");
            return;
        }

        System.out.print("New claimed date (YYYY-MM-DD): ");
        String claimedInput = scanner.nextLine().trim();
        Date claimedDate = parseDate(claimedInput);
        if (claimedDate == null) {
            return;
        }

        Date issueDate = getPaymentIssueDate(paymentId);
        if (issueDate == null) {
            System.out.println("[ERROR] Could not retrieve issue date.");
            return;
        }

        if (claimedDate.before(issueDate)) {
            System.out.println("[ERROR] Claimed date cannot be before issue date.");
            return;
        }

        String sql = "UPDATE UserPayments SET claimed_date = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, claimedDate);
            ps.setString(2, paymentId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("[SUCCESS] Claimed date updated.");
            } else {
                System.out.println("[ERROR] No payment updated.");
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void listUnclaimedPayments() {
        System.out.print("Start date (YYYY-MM-DD): ");
        Date startDate = parseDate(scanner.nextLine().trim());
        if (startDate == null) {
            return;
        }

        System.out.print("End date (YYYY-MM-DD): ");
        Date endDate = parseDate(scanner.nextLine().trim());
        if (endDate == null) {
            return;
        }

        if (endDate.before(startDate)) {
            System.out.println("[ERROR] End date must be on or after start date.");
            return;
        }

        String sql =
                "SELECT up.id, up.payment_type, up.amount, up.issue_date, up.claimed_date, up.person_id, " +
                "       up.edition_issue_id, " +
                "       CASE " +
                "           WHEN pub.title IS NULL AND ei.pub_date IS NULL THEN NULL " +
                "           WHEN ei.pub_date IS NULL THEN pub.title " +
                "           WHEN pub.title IS NULL THEN CAST(ei.pub_date AS CHAR) " +
                "           ELSE CONCAT(pub.title, ' - ', ei.pub_date) " +
                "       END AS related_work " +
                "FROM UserPayments up " +
                "LEFT JOIN EditionIssue ei ON ei.id = up.edition_issue_id " +
                "LEFT JOIN Publications pub ON pub.id = ei.publication_id " +
                "WHERE claimed_date IS NULL AND issue_date BETWEEN ? AND ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, startDate);
            ps.setDate(2, endDate);

            try (ResultSet rs = ps.executeQuery()) {
                printResultSetAsTable(rs);
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void compareIssues() {
        System.out.print("First issue ID: ");
        String issueId1 = scanner.nextLine().trim();

        System.out.print("Second issue ID: ");
        String issueId2 = scanner.nextLine().trim();

        if (!editionIssueExists(issueId1) || !editionIssueExists(issueId2)) {
            System.out.println("[ERROR] One or both issue IDs were not found.");
            return;
        }

        if (!isPeriodicIssue(issueId1) || !isPeriodicIssue(issueId2)) {
            System.out.println("[ERROR] Both IDs must refer to periodic issues, not book editions.");
            return;
        }

        String sql =
                "SELECT ei.id AS edition_issue_id, pub.title AS publication_title, " +
                "c.id, c.content_title AS title, c.topic, c.date_written " +
                "FROM Publications pub " +
                "JOIN EditionIssue ei ON ei.publication_id = pub.id " +
                "JOIN Content c ON c.edition_issue_id = ei.id " +
                "WHERE ei.id IN (?, ?) AND c.content_type = 'ARTICLE' " +
                "ORDER BY ei.id, c.id";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, issueId1);
            ps.setString(2, issueId2);

            try (ResultSet rs = ps.executeQuery()) {
                printResultSetAsTable(rs);
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void viewRelatedWorkForPerson() {
        System.out.print("Person ID: ");
        String personId = scanner.nextLine().trim();

        if (!personExists(personId)) {
            System.out.println("[ERROR] Person not found.");
            return;
        }

        String role = getPersonRole(personId);
        if (role == null) {
            System.out.println("[ERROR] Could not retrieve person role.");
            return;
        }

        String authoredWorkSql =
                "SELECT p.id AS person_id, p.name AS person_name, p.role, " +
                "       c.id AS content_id, c.content_title, c.content_type, c.topic, c.date_written, " +
                "       ei.id AS edition_issue_id, pub.title AS publication_title, ei.pub_date AS publication_date " +
                "FROM Person p " +
                "JOIN Writes w ON w.person_id = p.id " +
                "JOIN Content c ON c.id = w.content_id " +
                "JOIN EditionIssue ei ON ei.id = c.edition_issue_id " +
                "JOIN Publications pub ON pub.id = ei.publication_id " +
                "WHERE p.id = ? " +
                "ORDER BY c.date_written, c.id";

        String editedWorkSql =
                "SELECT p.id AS person_id, p.name AS person_name, p.role, " +
                "       pub.id AS publication_id, pub.title AS publication_title, pub.type AS publication_type, " +
                "       ei.id AS edition_issue_id, ei.pub_date AS publication_date, ei.edition_num, ei.isbn, ei.status " +
                "FROM Person p " +
                "JOIN Edits ed ON ed.person_id = p.id " +
                "JOIN Publications pub ON pub.id = ed.publication_id " +
                "JOIN EditionIssue ei ON ei.publication_id = pub.id " +
                "WHERE p.id = ? " +
                "ORDER BY pub.title, ei.pub_date, ei.id";

        try {
            if ("AUTHOR".equals(role) || "BOTH".equals(role)) {
                System.out.println("Authored Work:");
                try (PreparedStatement ps = conn.prepareStatement(authoredWorkSql)) {
                    ps.setString(1, personId);
                    try (ResultSet rs = ps.executeQuery()) {
                        printResultSetAsTableOrInfo(rs, "[INFO] No authored work found.");
                    }
                }
            }

            if ("EDITOR".equals(role) || "BOTH".equals(role)) {
                if ("BOTH".equals(role)) {
                    System.out.println();
                }
                System.out.println("Edited Work:");
                try (PreparedStatement ps = conn.prepareStatement(editedWorkSql)) {
                    ps.setString(1, personId);
                    try (ResultSet rs = ps.executeQuery()) {
                        printResultSetAsTableOrInfo(rs, "[INFO] No edited work found.");
                    }
                }
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

    private boolean editionIssueExists(String id) {
        String sql = "SELECT 1 FROM EditionIssue WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
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

    private boolean paymentExists(String paymentId) {
        String sql = "SELECT 1 FROM UserPayments WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, paymentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean isbnExists(String isbn) {
        String sql = "SELECT 1 FROM EditionIssue WHERE isbn = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean contentExistsForEditionIssue(String editionIssueId) {
        String sql = "SELECT 1 FROM Content WHERE edition_issue_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, editionIssueId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return true;
        }
    }

    private boolean ordersExistForEditionIssue(String editionIssueId) {
        String sql = "SELECT 1 FROM Orders WHERE edition_issue_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, editionIssueId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return true;
        }
    }

    private String getPublicationType(String publicationId) {
        String sql = "SELECT type FROM Publications WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, publicationId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("type") : null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    private String getPersonRole(String personId) {
        String sql = "SELECT role FROM Person WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, personId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("role") : null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    private String getPublicationTypeFromEditionIssue(String editionIssueId) {
        String sql = "SELECT p.type FROM EditionIssue ei " +
                     "JOIN Publications p ON ei.publication_id = p.id " +
                     "WHERE ei.id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, editionIssueId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    private String getPublicationTopicFromEditionIssue(String editionIssueId) {
        String sql = "SELECT p.primary_topic FROM EditionIssue ei " +
                     "JOIN Publications p ON ei.publication_id = p.id " +
                     "WHERE ei.id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, editionIssueId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (SQLException e) {
            return null;
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

    private Integer getEditionIssueEditionNum(String editionIssueId) {
        String sql = "SELECT edition_num FROM EditionIssue WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, editionIssueId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int value = rs.getInt("edition_num");
                    return rs.wasNull() ? null : value;
                }
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    private String getEditionIssueIsbn(String editionIssueId) {
        String sql = "SELECT isbn FROM EditionIssue WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, editionIssueId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("isbn") : null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    private String getContentTitle(String contentId) {
        String sql = "SELECT content_title FROM Content WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, contentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("content_title") : null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    private String getContentTopic(String contentId) {
        String sql = "SELECT topic FROM Content WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, contentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("topic") : null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    private Date getContentDateWritten(String contentId) {
        String sql = "SELECT date_written FROM Content WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, contentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDate("date_written") : null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    private String getEditionIssueIdForContent(String contentId) {
        String sql = "SELECT edition_issue_id FROM Content WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, contentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("edition_issue_id") : null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    private Date getPaymentIssueDate(String paymentId) {
        String sql = "SELECT issue_date FROM UserPayments WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, paymentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDate("issue_date") : null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    private boolean isPeriodicIssue(String editionIssueId) {
        String publicationType = getPublicationTypeFromEditionIssue(editionIssueId);
        return "JOURNAL".equals(publicationType) || "MAGAZINE".equals(publicationType);
    }

    private boolean isValidEditionStatus(String status) {
        return "IN_PROGRESS".equals(status) ||
               "FINISHED".equals(status) ||
               "PUBLISHED".equals(status) ||
               "UNPUBLISHED".equals(status);
    }

    private boolean isValidPaymentType(String paymentType) {
        return "BOOK_AUTHORSHIP".equals(paymentType) ||
               "ARTICLE_AUTHORSHIP".equals(paymentType) ||
               "EDITORIAL_WORK".equals(paymentType);
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

    private void printResultSetAsTableOrInfo(ResultSet rs, String emptyMessage) throws SQLException {
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
            System.out.println(emptyMessage);
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
