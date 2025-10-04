package proyectoprogra.export;

import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import proyectoprogra.model.Viaje;
import proyectoprogra.service.ViajeService;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Clase utilitaria para exportar los viajes a archivos Excel
 */
public class ExcelExporter {
    
    private final ViajeService viajeService;
    private final SimpleDateFormat dateFormat;
    
    public ExcelExporter() {
        this.viajeService = new ViajeService();
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    }
    
    /**
     * Exporta todos los viajes a un archivo Excel
     * El archivo se guarda con el formato: viajes_fecha_con_segundos.xlsx
     */
    public void exportarViajesAExcel() {
        try {
            // Obtener todos los viajes usando el ViajeService
            List<Viaje> viajes = viajeService.listAll();
            
            if (viajes.isEmpty()) {
                JOptionPane.showMessageDialog(null, 
                    "No hay viajes para exportar", 
                    "Información", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Generar nombre del archivo con fecha y segundos
            String nombreArchivo = generarNombreArchivo();
            
            // Crear el archivo Excel
            try (FileOutputStream outputStream = new FileOutputStream(nombreArchivo);
                 Workbook workbook = new Workbook(outputStream, "ViajesApp", "1.0")) {
                
                Worksheet worksheet = workbook.newWorksheet("Viajes");
                
                // Crear encabezados
                crearEncabezados(worksheet);
                
                // Llenar datos
                llenarDatos(worksheet, viajes);
                
                // Ajustar ancho de columnas
                ajustarAnchoColumnas(worksheet);
                
                workbook.finish();
                
                JOptionPane.showMessageDialog(null, 
                    "Excel exportado exitosamente como: " + nombreArchivo, 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                
            }
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, 
                "Error al exportar Excel: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Error inesperado al exportar Excel: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Genera el nombre del archivo con formato viajes_fecha_con_segundos.xlsx
     */
    private String generarNombreArchivo() {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        return "viajes_" + ahora.format(formatter) + ".xlsx";
    }
    
    /**
     * Crea los encabezados del Excel
     */
    private void crearEncabezados(Worksheet worksheet) {
        // Fila 0 (primera fila) para encabezados
        worksheet.value(0, 0, "ID");
        worksheet.value(0, 1, "Origen");
        worksheet.value(0, 2, "Destino");
        worksheet.value(0, 3, "Fecha Salida");
        worksheet.value(0, 4, "Fecha Llegada");
        worksheet.value(0, 5, "Estado");
        
        // Estilo para encabezados (negrita)
        for (int col = 0; col < 6; col++) {
            worksheet.style(0, col).bold().set();
        }
    }
    
    /**
     * Llena los datos de los viajes en el Excel
     */
    private void llenarDatos(Worksheet worksheet, List<Viaje> viajes) {
        for (int i = 0; i < viajes.size(); i++) {
            Viaje viaje = viajes.get(i);
            int fila = i + 1; // +1 porque la fila 0 son los encabezados
            
            worksheet.value(fila, 0, viaje.getId());
            worksheet.value(fila, 1, viaje.getOrigen() != null ? viaje.getOrigen() : "");
            worksheet.value(fila, 2, viaje.getDestino() != null ? viaje.getDestino() : "");
            worksheet.value(fila, 3, viaje.getFechaSalida() != null ? dateFormat.format(viaje.getFechaSalida()) : "");
            worksheet.value(fila, 4, viaje.getFechaLlegada() != null ? dateFormat.format(viaje.getFechaLlegada()) : "");
            worksheet.value(fila, 5, viaje.getEstado() != null ? viaje.getEstado() : "");
        }
    }
    
    /**
     * Ajusta el ancho de las columnas para mejor visualización
     */
    private void ajustarAnchoColumnas(Worksheet worksheet) {
        worksheet.width(0, 10);  // ID
        worksheet.width(1, 20);  // Origen
        worksheet.width(2, 20);  // Destino
        worksheet.width(3, 18);  // Fecha Salida
        worksheet.width(4, 18);  // Fecha Llegada
        worksheet.width(5, 15);  // Estado
    }
}