package proyectoprogra.gui.modals;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;
import proyectoprogra.model.Viaje;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MapaModal extends JDialog {

    private JMapViewer mapViewer;

    public MapaModal(Frame parent, Viaje viaje) {
        super(parent, "Ruta: " + viaje.getOrigen() + " → " + viaje.getDestino(), true);

        System.setProperty("http.agent", "GestorViajes/1.0");

        mapViewer = new JMapViewer();
        mapViewer.setTileSource(new OsmTileSource.Mapnik());
        mapViewer.setZoomControlsVisible(true);
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private Coordinate origen;
            private Coordinate destino;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    origen = geocodificarCiudad(viaje.getOrigen());
                    destino = geocodificarCiudad(viaje.getDestino());
                } catch (Exception e) {
                    e.printStackTrace();
                    origen = new Coordinate(14.6349, -90.5069);
                    destino = new Coordinate(14.6349, -90.5069);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    centrarMapa(origen, destino);
                    mapViewer.addMapMarker(new MapMarkerDot(origen.getLat(), origen.getLon()));
                    mapViewer.addMapMarker(new MapMarkerDot(destino.getLat(), destino.getLon()));
                    mapViewer.addMapPolygon(new MapPolygonImpl(Arrays.asList(origen, destino)));
                    
                    add(mapViewer, BorderLayout.CENTER);
                    setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    mostrarError("Error al mostrar el mapa: " + e.getMessage());
                }
            }
        };

        worker.execute();

        setSize(800, 600);
        setLocationRelativeTo(parent);
    }

    private Coordinate geocodificarCiudad(String nombreCiudad) throws Exception {
        if (nombreCiudad == null || nombreCiudad.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre de ciudad vacío");
        }

        String ciudadCodificada = URLEncoder.encode(nombreCiudad.trim(), StandardCharsets.UTF_8);
        String urlString = "https://nominatim.openstreetmap.org/search?format=json&q=" + ciudadCodificada + "&limit=1";
        
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        connection.setRequestProperty("User-Agent", "GestorViajes/1.0 (rodrigogerardocardenas@gmail.com)");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(10000);
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            
            String jsonResponse = response.toString();
            
            if (jsonResponse.contains("\"lat\"") && jsonResponse.contains("\"lon\"")) {
                String latPattern = "\"lat\":\"([^\"]+)\"";
                String lonPattern = "\"lon\":\"([^\"]+)\"";
                
                java.util.regex.Pattern latRegex = java.util.regex.Pattern.compile(latPattern);
                java.util.regex.Pattern lonRegex = java.util.regex.Pattern.compile(lonPattern);
                
                java.util.regex.Matcher latMatcher = latRegex.matcher(jsonResponse);
                java.util.regex.Matcher lonMatcher = lonRegex.matcher(jsonResponse);
                
                if (latMatcher.find() && lonMatcher.find()) {
                    double lat = Double.parseDouble(latMatcher.group(1));
                    double lon = Double.parseDouble(lonMatcher.group(1));
                    return new Coordinate(lat, lon);
                }
            }
            
            throw new Exception("No se encontraron coordenadas para: " + nombreCiudad);
            
        } finally {
            connection.disconnect();
        }
    }

    private void mostrarError(String mensaje) {
        JLabel error = new JLabel("<html><center>" + mensaje + "</center></html>", JLabel.CENTER);
        error.setFont(new Font("Arial", Font.BOLD, 14));
        error.setForeground(Color.RED);
        mapViewer.add(error, BorderLayout.CENTER);
        mapViewer.revalidate();
        mapViewer.repaint();
    }

    private void centrarMapa(Coordinate origen, Coordinate destino) {
        double latPromedio = (origen.getLat() + destino.getLat()) / 2.0;
        double lonPromedio = (origen.getLon() + destino.getLon()) / 2.0;
        Coordinate centro = new Coordinate(latPromedio, lonPromedio);
        
        mapViewer.setDisplayPosition(centro, 6);
    }

}
