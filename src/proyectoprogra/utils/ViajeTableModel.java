package proyectoprogra.utils;

import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import proyectoprogra.model.Viaje;

/**
 * Modelo de tabla personalizado para mostrar viajes en un JTable.
 * 
 * Esta clase extiende AbstractTableModel de Swing y implementa el patrón MVC
 * (Model-View-Controller) para separar los datos de la presentación visual.
 * 
 * Funcionalidades principales:
 * - Muestra datos de viajes en formato tabular
 * - Permite edición solo en la columna de "Acciones"
 * - Formateo automático de fechas
 * - Actualización dinámica de datos
 * - Integración con ViajeManager para obtener datos
 * 
 * Estructura de columnas: ID, Origen, Destino, Fecha Salida, Fecha Llegada, Estado, Acciones
 */
public class ViajeTableModel extends AbstractTableModel {
    // Nombres de las columnas que se mostrarán en el encabezado de la tabla
    private static final String[] COLUMN_NAMES = {"ID", "Origen", "Destino", "Fecha Salida", "Fecha Llegada", "Estado", "Acciones"};
    
    // Tipos de datos para cada columna (usado por JTable para renderizado y ordenamiento)
    private static final Class<?>[] COLUMN_CLASSES = {Integer.class, String.class, String.class, String.class, String.class, String.class, String.class};
    
    // Lista de viajes que contiene los datos de la tabla
    private List<Viaje> viajes;
    
    // Formateador para mostrar fechas en formato legible (dd/MM/yyyy HH:mm)
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    /**
     * Constructor por defecto que carga todos los viajes desde el ViajeManager.
     * 
     * Este constructor es útil cuando se quiere mostrar todos los viajes
     * disponibles en la base de datos al crear la tabla.
     */
    public ViajeTableModel() {
        this.viajes = ViajeManager.obtenerTodosLosViajes();
    }
    
    /**
     * Constructor que recibe una lista específica de viajes.
     * 
     * Este constructor es útil cuando se quiere mostrar un subconjunto
     * de viajes o cuando los datos ya han sido filtrados/procesados.
     * 
     * @param viajes Lista de viajes a mostrar en la tabla
     */
    public ViajeTableModel(List<Viaje> viajes) {
        this.viajes = viajes;
    }
    
    /**
     * Actualiza la lista de viajes y notifica a la tabla para que se redibuje.
     * 
     * Este método es útil cuando se tienen nuevos datos desde otra fuente
     * (como resultado de una búsqueda o filtrado).
     * 
     * @param viajes Nueva lista de viajes a mostrar
     */
    public void setViajes(List<Viaje> viajes) {
        this.viajes = viajes;
        // Notificar a todos los listeners que los datos han cambiado
        fireTableDataChanged();
    }
    
    /**
     * Refresca los datos de la tabla obteniéndolos nuevamente desde la base de datos.
     * 
     * Este método es útil después de operaciones CRUD (crear, actualizar, eliminar)
     * para asegurar que la tabla muestre los datos más actuales.
     */
    public void refrescarTabla() {
        this.viajes = ViajeManager.obtenerTodosLosViajes();
        // Notificar a la tabla que todos los datos han cambiado
        fireTableDataChanged();
    }
    
    /**
     * Retorna el número total de filas en la tabla.
     * 
     * @return Número de viajes en la lista (filas de la tabla)
     */
    @Override
    public int getRowCount() {
        return viajes.size();
    }
    
    /**
     * Retorna el número total de columnas en la tabla.
     * 
     * @return Número de columnas definidas en COLUMN_NAMES
     */
    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }
    
    /**
     * Retorna el nombre de la columna para mostrar en el encabezado.
     * 
     * @param column Índice de la columna (0-based)
     * @return Nombre de la columna
     */
    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }
    
    /**
     * Retorna el tipo de datos de la columna especificada.
     * 
     * Esto ayuda a JTable a aplicar el renderizador y editor apropiados
     * (por ejemplo, alineación de números, formato de fechas, etc.).
     * 
     * @param columnIndex Índice de la columna (0-based)
     * @return Clase del tipo de datos de la columna
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return COLUMN_CLASSES[columnIndex];
    }
    
    /**
     * Retorna el valor de una celda específica de la tabla.
     * 
     * Este es el método más importante del TableModel ya que define
     * qué datos se muestran en cada celda de la tabla.
     * 
     * Mapeo de columnas:
     * 0: ID del viaje (Integer)
     * 1: Origen (String)
     * 2: Destino (String)
     * 3: Fecha de salida (String formateada)
     * 4: Fecha de llegada (String formateada)
     * 5: Estado del viaje (String)
     * 6: Acciones (String vacía - se manejan con botones personalizados)
     * 
     * @param rowIndex Índice de la fila (0-based)
     * @param columnIndex Índice de la columna (0-based)
     * @return Valor de la celda o null si los índices son inválidos
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        // Validar que el índice de fila sea válido
        if (rowIndex >= 0 && rowIndex < viajes.size()) {
            Viaje viaje = viajes.get(rowIndex);
            
            switch (columnIndex) {
                case 0: return viaje.getId();                                    // ID
                case 1: return viaje.getOrigen();                               // Origen
                case 2: return viaje.getDestino();                              // Destino
                case 3: return dateFormat.format(viaje.getFechaSalida());       // Fecha Salida
                case 4: return dateFormat.format(viaje.getFechaLlegada());      // Fecha Llegada
                case 5: return viaje.getEstado();                               // Estado
                case 6: return "";                                              // Acciones (botones personalizados)
                default: return null;                                            // Columna inválida
            }
        }
        return null;
    }
    
    /**
     * Determina si una celda es editable.
     * 
     * En este modelo, solo la columna de "Acciones" (columna 6) es editable,
     * ya que contiene botones para editar y eliminar viajes.
     * 
     * @param rowIndex Índice de la fila
     * @param columnIndex Índice de la columna
     * @return true solo si es la columna de acciones (6), false en otros casos
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 6; // Solo la columna "Acciones" es editable
    }
    
    /**
     * Método para establecer valores en celdas editables.
     * 
     * Actualmente vacío porque la edición de datos se maneja a través
     * de formularios separados, no directamente en la tabla.
     * 
     * @param aValue Nuevo valor a establecer
     * @param rowIndex Índice de la fila
     * @param columnIndex Índice de la columna
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // Método vacío - la edición se maneja en formularios separados
    }
    
    /**
     * Obtiene el objeto Viaje de una fila específica.
     * 
     * Método de conveniencia para obtener el viaje completo cuando
     * se conoce el índice de la fila (por ejemplo, al hacer clic en una fila).
     * 
     * @param rowIndex Índice de la fila (0-based)
     * @return Objeto Viaje de la fila especificada o null si el índice es inválido
     */
    public Viaje getViajeAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < viajes.size()) {
            return viajes.get(rowIndex);
        }
        return null;
    }
    
    /**
     * Busca el índice de fila de un viaje específico.
     * 
     * Útil para seleccionar o resaltar una fila específica en la tabla
     * cuando se conoce el objeto Viaje.
     * 
     * @param viaje El objeto Viaje a buscar
     * @return Índice de la fila donde se encuentra el viaje, o -1 si no se encuentra
     */
    public int getIndexOfViaje(Viaje viaje) {
        return viajes.indexOf(viaje);
    }
}
