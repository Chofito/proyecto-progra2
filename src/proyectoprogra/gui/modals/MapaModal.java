package proyectoprogra.gui.modals;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.Coordinate;

import javax.swing.*;
import java.awt.*;

public class MapaModal extends JDialog {

    private JMapViewer mapViewer;

    public MapaModal(Frame owner) {
        super(owner, "Mapa del Viaje", true);
        init();
    }

    private void init() {
        // Crear el visor de mapa
        mapViewer = new JMapViewer();

        // Establecer posición inicial (Guatemala)
        Coordinate guatemala = new Coordinate(14.6349, -90.5069);
        mapViewer.setDisplayPosition(guatemala, 7); // zoom inicial = 7

        // Fondo gris si no carga nada
        mapViewer.setBackground(Color.LIGHT_GRAY);

        // Agregar al dialog
        getContentPane().add(new JScrollPane(mapViewer), BorderLayout.CENTER);

        // Configuración de ventana
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MapaModal modal = new MapaModal(null);
            modal.setVisible(true);
        });
    }
}
