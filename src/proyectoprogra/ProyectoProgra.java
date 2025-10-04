package proyectoprogra;

// Importaciones necesarias para la aplicación
import javax.swing.SwingUtilities;  // Ventana principal de la aplicación
import javax.swing.UIManager; // Conector para base de datos Oracle
import proyectoprogra.database.OracleConnector;              // Utilidades para trabajar con Swing
import proyectoprogra.gui.frames.ViajeFrame;                   // Gestor de apariencia de la interfaz

/**
 * Clase principal de la aplicación de Gestión de Viajes
 * Esta clase contiene el método main que inicializa toda la aplicación
 */
public class ProyectoProgra {

    /**
     * Método principal que inicia la aplicación
     */
    public static void main(String[] args) {
        // Mensaje de bienvenida en consola
        System.out.println("=== INICIANDO APLICACIÓN DE GESTIÓN DE VIAJES ===");
        
        // Verificar si hay conexión disponible con la base de datos Oracle
        System.out.println("Verificando conexión a la base de datos...");
        if (!OracleConnector.verifyConnection()) {
            // Si no hay conexión, mostrar advertencia pero continuar con la aplicación
            System.err.println("No se pudo establecer conexión con la base de datos.");
            System.err.println("La aplicación puede no funcionar correctamente.");
            try {
                // Pausa para que el usuario pueda leer el mensaje de error
                System.in.read();
            } catch (Exception e) {
            }
        }
        
        // Configurar la apariencia visual de la aplicación
        try {
            // Establecer el look and feel nativo del sistema operativo
            // Esto hace que la aplicación se vea como las aplicaciones nativas del OS
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si falla, continúa con el look and feel por defecto
            System.err.println("Error al configurar el look and feel: " + e.getMessage());
        }
        
        // Lanzar la interfaz gráfica en el Event Dispatch Thread (EDT)
        // SwingUtilities.invokeLater() garantiza que la GUI se ejecute en el hilo correcto
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Crear y mostrar la ventana principal de la aplicación
                    ViajeFrame frame = new ViajeFrame();
                    frame.setVisible(true);
                    System.out.println("Aplicación de Gestión de Viajes iniciada exitosamente");
                } catch (Exception e) {
                    // Manejo de errores durante la inicialización de la GUI
                    System.err.println("Error al iniciar la aplicación: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}
