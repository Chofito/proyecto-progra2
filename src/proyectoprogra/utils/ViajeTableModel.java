package proyectoprogra.utils;

import proyectoprogra.model.Viaje;
import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.List;

public class ViajeTableModel extends AbstractTableModel {
    private static final String[] COLUMN_NAMES = {"ID", "Origen", "Destino", "Fecha Salida", "Fecha Llegada", "Estado", "Acciones"};
    private static final Class<?>[] COLUMN_CLASSES = {Integer.class, String.class, String.class, String.class, String.class, String.class, String.class};
    
    private List<Viaje> viajes;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    public ViajeTableModel() {
        this.viajes = ViajeManager.obtenerTodosLosViajes();
    }
    
    public ViajeTableModel(List<Viaje> viajes) {
        this.viajes = viajes;
    }
    
    public void setViajes(List<Viaje> viajes) {
        this.viajes = viajes;
        fireTableDataChanged();
    }
    
    public void refrescarTabla() {
        this.viajes = ViajeManager.obtenerTodosLosViajes();
        fireTableDataChanged();
    }
    
    @Override
    public int getRowCount() {
        return viajes.size();
    }
    
    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return COLUMN_CLASSES[columnIndex];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= 0 && rowIndex < viajes.size()) {
            Viaje viaje = viajes.get(rowIndex);
            
            switch (columnIndex) {
                case 0: return viaje.getId();
                case 1: return viaje.getOrigen();
                case 2: return viaje.getDestino();
                case 3: return dateFormat.format(viaje.getFechaSalida());
                case 4: return dateFormat.format(viaje.getFechaLlegada());
                case 5: return viaje.getEstado();
                case 6: return "";
                default: return null;
            }
        }
        return null;
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 6;
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }
    
    public Viaje getViajeAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < viajes.size()) {
            return viajes.get(rowIndex);
        }
        return null;
    }
    
    public int getIndexOfViaje(Viaje viaje) {
        return viajes.indexOf(viaje);
    }
}
