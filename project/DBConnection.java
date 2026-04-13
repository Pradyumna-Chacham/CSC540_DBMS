import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Update these to match your MariaDB setup
    static final String jdbcURL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/pchacha2";
    private static final String USER = "pchacha2";
    private static final String PASSWORD = "Prady@sql03";

    private DBConnection() {
        // Prevent instantiation
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcURL, USER, PASSWORD);
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
