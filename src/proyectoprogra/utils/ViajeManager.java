package proyectoprogra.utils;

import proyectoprogra.model.Viaje;
import proyectoprogra.service.ViajeService;
import java.util.ArrayList;
import java.util.List;

public class ViajeManager {
    private static final ViajeService viajeService = new ViajeService();
    private static final List<Viaje> viajes = new ArrayList<>();
    
    public static boolean agregarViaje(Viaje viaje) {
        if (viaje == null) {
            return false;
        }
        try {
            viajeService.create(viaje);
            inicializarDesdeBaseDatos();
            return true;
        } catch (Exception e) {
            System.err.println("Error al agregar viaje a la base de datos: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean eliminarViaje(int index) {
        if (index >= 0 && index < viajes.size()) {
            Viaje viaje = viajes.get(index);
            try {
                boolean eliminado = viajeService.delete(viaje.getId());
                
                if (eliminado) {
                    inicializarDesdeBaseDatos();
                    return true;
                }
                return false;
            } catch (Exception e) {
                System.err.println("Error al eliminar viaje de la base de datos: " + e.getMessage());
                return false;
            }
        }
        return false;
    }
    
    public static boolean actualizarViaje(Viaje viaje) {
        if (viaje == null) {
            return false;
        }
        try {
            Viaje viajeActualizado = viajeService.update(viaje);
            if (viajeActualizado != null) {
                inicializarDesdeBaseDatos();
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error al actualizar viaje en la base de datos: " + e.getMessage());
            return false;
        }
    }
    
    
    public static List<Viaje> obtenerTodosLosViajes() {
        try {
            List<Viaje> viajesDB = viajeService.listAll();
            
            viajes.clear();
            viajes.addAll(viajesDB);
            
            return new ArrayList<>(viajes);
        } catch (Exception e) {
            System.err.println("Error al cargar viajes de la base de datos: " + e.getMessage());
            return new ArrayList<>(viajes);
        }
    }
    
    public static Viaje obtenerViaje(int index) {
        if (index >= 0 && index < viajes.size()) {
            return viajes.get(index);
        }
        return null;
    }
    
    public static List<Viaje> buscarViajes(String busqueda) {
        List<Viaje> resultados = new ArrayList<>();
        if (busqueda == null || busqueda.trim().isEmpty()) {
            return resultados;
        }
        
        String busquedaLower = busqueda.toLowerCase().trim();
        for (Viaje viaje : viajes) {
            if (viaje.getOrigen().toLowerCase().contains(busquedaLower) ||
                viaje.getDestino().toLowerCase().contains(busquedaLower)) {
                resultados.add(viaje);
            }
        }
        
        return resultados;
    }
    
    
    public static void inicializarDesdeBaseDatos() {
        try {
            List<Viaje> viajesDB = viajeService.listAll();
            viajes.clear();
            viajes.addAll(viajesDB);
            System.out.println("Viajes cargados desde la base de datos: " + viajes.size());
        } catch (Exception e) {
            System.err.println("Error al inicializar viajes desde la base de datos: " + e.getMessage());
        }
    }
}
