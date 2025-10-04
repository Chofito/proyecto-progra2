package proyectoprogra;

import proyectoprogra.gui.frames.ViajeFrame;
import proyectoprogra.database.OracleConnector;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class ProyectoProgra {

    public static void main(String[] args) {
        System.out.println("=== INICIANDO APLICACIÓN DE GESTIÓN DE VIAJES ===");
        
        System.out.println("Verificando conexión a la base de datos...");
        if (!OracleConnector.verifyConnection()) {
            System.err.println("No se pudo establecer conexión con la base de datos.");
            System.err.println("La aplicación puede no funcionar correctamente.");
            try {
                System.in.read();
            } catch (Exception e) {
            }
        }
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error al configurar el look and feel: " + e.getMessage());
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    ViajeFrame frame = new ViajeFrame();
                    frame.setVisible(true);
                    System.out.println("Aplicación de Gestión de Viajes iniciada exitosamente");
                } catch (Exception e) {
                    System.err.println("Error al iniciar la aplicación: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}
