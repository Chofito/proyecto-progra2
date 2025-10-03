package proyectoprogra.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleConnector {
    private static final String URL = "jdbc:oracle:thin:@//localhost:1521/XEPDB1";
    private static final String USER = "ProyectoProgramacion";
    private static final String PASSWORD = "prog123";
    
    private static boolean connectionVerified = false;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean verifyConnection() {
        try (Connection conn = getConnection()) {
            connectionVerified = true;
            System.out.println("Conexión a Oracle Database establecida correctamente");
            return true;
        } catch (SQLException e) {
            connectionVerified = false;
            System.err.println("Error al conectar con Oracle Database:");
            System.err.println("  " + e.getMessage());
            return false;
        }
    }
    
    public static boolean isConnectionVerified() {
        return connectionVerified;
    }
    
    public static boolean reVerifyConnection() {
        return verifyConnection();
    }
}

