import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for database connectivity.
 *
 * This class supplies a single method for obtaining a JDBC connection
 * and a helper method to validate the connection without requiring
 * an instance of the class.
 */
public class DBConnection {

    // Update these to match your MariaDB setup
    static final String jdbcURL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/pchacha2";
    private static final String USER = "pchacha2";
    private static final String PASSWORD = "Prady@sql03";

    private DBConnection() {
        // Prevent instantiation of this utility class
    }

    /**
     * Open a new JDBC connection using the configured URL, user, and password.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcURL, USER, PASSWORD);
    }

    /**
     * Check that the database connection can be established successfully.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
