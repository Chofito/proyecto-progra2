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
 * Clase utilitaria para exportar los viajes a archivos Excel.
 * Utiliza la librería FastExcel para generar archivos .xlsx
 * con formato profesional y nombres automáticos basados en fecha.
 */
public class ExcelExporter {
    
    // Servicio para obtener datos de viajes desde la base de datos
    private final ViajeService viajeService;
    // Formateador para mostrar fechas en formato legible
    private final SimpleDateFormat dateFormat;
    
    // Constructor que inicializa el servicio y el formato de fecha
    public ExcelExporter() {
        this.viajeService = new ViajeService();
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    }
    
    /**
     * Exporta todos los viajes a un archivo Excel.
     * El archivo se guarda con el formato: viajes_fecha_con_segundos.xlsx
     * Muestra mensajes de éxito o error al usuario.
     */
    public void exportarViajesAExcel() {
        try {
            // Obtener todos los viajes usando el ViajeService
            List<Viaje> viajes = viajeService.listAll();
            
            // Verificar si hay datos para exportar
            if (viajes.isEmpty()) {
                JOptionPane.showMessageDialog(null, 
                    "No hay viajes para exportar", 
                    "Información", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Generar nombre del archivo con fecha y segundos
            String nombreArchivo = generarNombreArchivo();
            
            // Crear el archivo Excel usando try-with-resources
            try (FileOutputStream outputStream = new FileOutputStream(nombreArchivo);
                 Workbook workbook = new Workbook(outputStream, "ViajesApp", "1.0")) {
                
                // Crear hoja de trabajo
                Worksheet worksheet = workbook.newWorksheet("Viajes");
                
                // Crear encabezados con formato
                crearEncabezados(worksheet);
                
                // Llenar datos de los viajes
                llenarDatos(worksheet, viajes);
                
                // Ajustar ancho de columnas para mejor visualización
                ajustarAnchoColumnas(worksheet);
                
                // Finalizar y guardar el archivo
                workbook.finish();
                
                JOptionPane.showMessageDialog(null, 
                    "Excel exportado exitosamente como: " + nombreArchivo, 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                
            }
            
        } catch (IOException e) {
            // Error de escritura de archivo
            JOptionPane.showMessageDialog(null, 
                "Error al exportar Excel: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            // Cualquier otro error inesperado
            JOptionPane.showMessageDialog(null, 
                "Error inesperado al exportar Excel: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Genera el nombre del archivo con formato viajes_fecha_con_segundos.xlsx
     * Ejemplo: viajes_20241003_143025.xlsx
     */
    private String generarNombreArchivo() {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        return "viajes_" + ahora.format(formatter) + ".xlsx";
    }
    
    /**
     * Crea los encabezados del Excel con formato en negrita.
     * Define las 6 columnas principales del reporte.
     */
    private void crearEncabezados(Worksheet worksheet) {
        // Fila 0 (primera fila) para encabezados
        worksheet.value(0, 0, "ID");
        worksheet.value(0, 1, "Origen");
        worksheet.value(0, 2, "Destino");
        worksheet.value(0, 3, "Fecha Salida");
        worksheet.value(0, 4, "Fecha Llegada");
        worksheet.value(0, 5, "Estado");
        
        // Aplicar estilo negrita a todos los encabezados
        for (int col = 0; col < 6; col++) {
            worksheet.style(0, col).bold().set();
        }
    }
    
    /**
     * Llena los datos de los viajes en el Excel.
     * Maneja valores null de forma segura reemplazándolos por cadenas vacías.
     */
    private void llenarDatos(Worksheet worksheet, List<Viaje> viajes) {
        for (int i = 0; i < viajes.size(); i++) {
            Viaje viaje = viajes.get(i);
            int fila = i + 1; // +1 porque la fila 0 son los encabezados
            
            // Llenar cada columna con verificación de valores null
            worksheet.value(fila, 0, viaje.getId());
            worksheet.value(fila, 1, viaje.getOrigen() != null ? viaje.getOrigen() : "");
            worksheet.value(fila, 2, viaje.getDestino() != null ? viaje.getDestino() : "");
            worksheet.value(fila, 3, viaje.getFechaSalida() != null ? dateFormat.format(viaje.getFechaSalida()) : "");
            worksheet.value(fila, 4, viaje.getFechaLlegada() != null ? dateFormat.format(viaje.getFechaLlegada()) : "");
            worksheet.value(fila, 5, viaje.getEstado() != null ? viaje.getEstado() : "");
        }
    }
    
    /**
     * Ajusta el ancho de las columnas para mejor visualización.
     * Los anchos están optimizados según el contenido típico de cada campo.
     */
    private void ajustarAnchoColumnas(Worksheet worksheet) {
        worksheet.width(0, 10);  // ID - números cortos
        worksheet.width(1, 20);  // Origen - nombres de ciudades
        worksheet.width(2, 20);  // Destino - nombres de ciudades
        worksheet.width(3, 18);  // Fecha Salida - formato dd/MM/yyyy HH:mm
        worksheet.width(4, 18);  // Fecha Llegada - formato dd/MM/yyyy HH:mm
        worksheet.width(5, 15);  // Estado - "En curso" o "Pendiente"
    }
}