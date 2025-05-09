import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbCon {
    private static final String URL = "jdbc:mysql://mainline.proxy.rlwy.net:34739/railway";
    private static final String USER = "root";
    private static final String PASSWORD = "ePZWkvXduIzqxqZoTjRcbJcJWEznWNWw";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
