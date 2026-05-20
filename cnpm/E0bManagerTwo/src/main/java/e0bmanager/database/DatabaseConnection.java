package e0bmanager.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Thông tin cấu hình Database
    private static final String URL = "jdbc:mysql://localhost:3306/E0bmanager_DB";
    private static final String USER = "root";
    private static final String PASS = "";

    /**
     * Hàm lấy kết nối đến Database
     * @return Connection object
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Kết nối Database thành công!");
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy Driver JDBC: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối SQL: " + e.getMessage());
        }
        return conn;
    }}
