package proyectoprogra.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase utilitaria para manejar las conexiones a la base de datos Oracle.
 * 
 * Esta clase implementa el patrón Singleton implícito para la configuración
 * de conexión y proporciona métodos estáticos para obtener conexiones
 * y verificar el estado de la base de datos.
 * 
 * Configuración actual:
 * - Servidor: localhost:1521
 * - SID: XE (Oracle Express Edition)
 * - Usuario: system
 * - Contraseña: Oracle123
 */
public class OracleConnector {
    // URL de conexión a Oracle Database usando el driver thin
    // Formato: jdbc:oracle:thin:@servidor:puerto:SID
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE";
    
    // Credenciales de la base de datos
    // NOTA: En producción, estas credenciales deberían estar en variables de entorno
    private static final String USER = "system";
    private static final String PASSWORD = "Oracle123";
    
    // Flag para controlar si la conexión ha sido verificada al menos una vez
    private static boolean connectionVerified = false;

    /**
     * Obtiene una nueva conexión a la base de datos Oracle.
     * 
     * Este método crea una nueva conexión cada vez que se llama.
     * Es responsabilidad del código cliente cerrar la conexión después de usarla.
     * Se recomienda usar try-with-resources para el manejo automático de recursos.
     * 
     * @return Una nueva conexión a la base de datos Oracle
     * @throws SQLException si no se puede establecer la conexión
     * 
     * Ejemplo de uso:
     * try (Connection conn = OracleConnector.getConnection()) {
     *     // usar la conexión
     * } // se cierra automáticamente
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Verifica si es posible establecer una conexión con la base de datos.
     * 
     * Este método intenta crear una conexión y la cierra inmediatamente.
     * Actualiza el estado interno (connectionVerified) y muestra mensajes
     * informativos en la consola.
     * 
     * @return true si la conexión es exitosa, false en caso contrario
     * 
     * Casos de fallo comunes:
     * - Servidor Oracle no está ejecutándose
     * - Puerto 1521 bloqueado o en uso
     * - Credenciales incorrectas
     * - SID 'XE' no existe
     * - Driver JDBC no encontrado en el classpath
     */
    public static boolean verifyConnection() {
        try (Connection conn = getConnection()) {
            // La conexión se crea y cierra automáticamente - no necesitamos usarla
            // Solo el hecho de crearla exitosamente confirma que la BD está disponible
            connectionVerified = true;
            System.out.println("Conexión a Oracle Database establecida correctamente");
            return true;
        } catch (SQLException e) {
            // Marcar la conexión como fallida y mostrar error detallado
            connectionVerified = false;
            System.err.println("Error al conectar con Oracle Database:");
            System.err.println("  " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Indica si la conexión fue verificada exitosamente en algún momento.
     * 
     * Este método NO realiza una nueva verificación, solo retorna el estado
     * de la última verificación realizada con verifyConnection().
     * 
     * @return true si la última verificación fue exitosa, false en caso contrario
     * 
     * IMPORTANTE: Este método no garantiza que la conexión siga siendo válida
     * en el momento actual, solo indica el resultado de la última verificación.
     */
    public static boolean isConnectionVerified() {
        return connectionVerified;
    }
    
    /**
     * Fuerza una nueva verificación de la conexión, independientemente
     * del estado anterior.
     * 
     * Este método es útil cuando:
     * - Se quiere volver a probar después de un fallo
     * - Se han hecho cambios en la configuración de la base de datos
     * - Se quiere refrescar el estado de conexión
     * 
     * @return true si la nueva verificación es exitosa, false en caso contrario
     * 
     * Es equivalente a llamar verifyConnection() directamente.
     */
    public static boolean reVerifyConnection() {
        return verifyConnection();
    }
}