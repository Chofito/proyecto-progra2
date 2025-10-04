package proyectoprogra.utils;

import java.util.ArrayList;
import java.util.List;
import proyectoprogra.model.Viaje;
import proyectoprogra.service.ViajeService;

/**
 * Clase utilitaria que actúa como intermediario entre la interfaz gráfica
 * y el servicio de datos de Viajes.
 * 
 * Esta clase implementa el patrón Facade/Manager y proporciona:
 * - Métodos simplificados para operaciones CRUD
 * - Gestión de cache local de viajes
 * - Manejo centralizado de errores
 * - Funcionalidades adicionales como búsqueda
 * - Sincronización entre base de datos y memoria
 * 
 * Todas las operaciones son estáticas para facilitar el acceso desde
 * cualquier parte de la aplicación sin necesidad de instanciar objetos.
 * 
 * Responsabilidades:
 * - Delegar operaciones CRUD al ViajeService
 * - Mantener una copia local actualizada de los viajes
 * - Proporcionar funcionalidades de búsqueda y filtrado
 * - Manejar errores y proporcionar retroalimentación
 */
public class ViajeManager {
    // Instancia del servicio de datos para operaciones con la base de datos
    private static final ViajeService viajeService = new ViajeService();
    
    // Cache local de viajes para mejorar el rendimiento y reducir consultas a la BD
    // Se mantiene sincronizado con la base de datos después de cada operación
    private static final List<Viaje> viajes = new ArrayList<>();
    
    /**
     * Agrega un nuevo viaje a la base de datos y actualiza el cache local.
     * 
     * Este método valida el viaje, lo guarda en la base de datos y
     * refresca el cache local para mantener la consistencia.
     * 
     * @param viaje El objeto Viaje a agregar (no debe ser null)
     * @return true si el viaje se agregó exitosamente, false en caso contrario
     * 
     * Casos de fallo:
     * - viaje es null
     * - Error de validación en ViajeService
     * - Error de conexión a la base de datos
     * - Restricciones de integridad en la BD
     */
    public static boolean agregarViaje(Viaje viaje) {
        // Validación básica de parámetros
        if (viaje == null) {
            return false;
        }
        try {
            // Delegar la creación al servicio de datos
            viajeService.create(viaje);
            // Actualizar cache local con los datos más recientes
            inicializarDesdeBaseDatos();
            return true;
        } catch (Exception e) {
            // Registrar error y retornar fallo
            System.err.println("Error al agregar viaje a la base de datos: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Elimina un viaje por su índice en el cache local.
     * 
     * Este método obtiene el viaje del cache, lo elimina de la base de datos
     * usando su ID y actualiza el cache local.
     * 
     * @param index Índice del viaje en la lista local (0-based)
     * @return true si el viaje se eliminó exitosamente, false en caso contrario
     * 
     * Casos de fallo:
     * - Índice fuera de rango
     * - Viaje no existe en la base de datos
     * - Error de conexión a la base de datos
     * - Restricciones de integridad (viaje referenciado por otras tablas)
     */
    public static boolean eliminarViaje(int index) {
        // Validar que el índice esté dentro del rango válido
        if (index >= 0 && index < viajes.size()) {
            // Obtener el viaje del cache local
            Viaje viaje = viajes.get(index);
            try {
                // Intentar eliminar de la base de datos usando el ID del viaje
                boolean eliminado = viajeService.delete(viaje.getId());
                
                if (eliminado) {
                    // Actualizar cache local para reflejar la eliminación
                    inicializarDesdeBaseDatos();
                    return true;
                }
                return false;
            } catch (Exception e) {
                // Registrar error y retornar fallo
                System.err.println("Error al eliminar viaje de la base de datos: " + e.getMessage());
                return false;
            }
        }
        // Índice inválido
        return false;
    }
    
    /**
     * Actualiza un viaje existente en la base de datos.
     * 
     * Este método valida el viaje, lo actualiza en la base de datos
     * y refresca el cache local para mantener la consistencia.
     * 
     * @param viaje El objeto Viaje con los datos actualizados (debe tener ID válido)
     * @return true si el viaje se actualizó exitosamente, false en caso contrario
     * 
     * Casos de fallo:
     * - viaje es null
     * - viaje no tiene ID válido
     * - Error de validación en ViajeService
     * - Viaje no existe en la base de datos
     * - Error de conexión a la base de datos
     */
    public static boolean actualizarViaje(Viaje viaje) {
        // Validación básica de parámetros
        if (viaje == null) {
            return false;
        }
        try {
            // Delegar la actualización al servicio de datos
            Viaje viajeActualizado = viajeService.update(viaje);
            if (viajeActualizado != null) {
                // Actualizar cache local con los datos más recientes
                inicializarDesdeBaseDatos();
                return true;
            }
            return false;
        } catch (Exception e) {
            // Registrar error y retornar fallo
            System.err.println("Error al actualizar viaje en la base de datos: " + e.getMessage());
            return false;
        }
    }
    
    
    /**
     * Obtiene todos los viajes desde la base de datos y actualiza el cache local.
     * 
     * Este método siempre consulta la base de datos para obtener los datos
     * más actuales, actualiza el cache local y retorna una copia de la lista.
     * 
     * @return Lista de todos los viajes (copia independiente del cache interno)
     * 
     * Comportamiento en caso de error:
     * - Si hay error de BD, retorna el contenido actual del cache local
     * - Si no hay datos en cache y falla la BD, retorna lista vacía
     * 
     * NOTA: Siempre retorna una nueva instancia de ArrayList para evitar
     * modificaciones accidentales del cache interno.
     */
    public static List<Viaje> obtenerTodosLosViajes() {
        try {
            // Obtener datos frescos desde la base de datos
            List<Viaje> viajesDB = viajeService.listAll();
            
            // Actualizar cache local
            viajes.clear();
            viajes.addAll(viajesDB);
            
            // Retornar copia independiente para evitar modificaciones externas
            return new ArrayList<>(viajes);
        } catch (Exception e) {
            // En caso de error, usar datos del cache local (si los hay)
            System.err.println("Error al cargar viajes de la base de datos: " + e.getMessage());
            return new ArrayList<>(viajes);
        }
    }
    
    /**
     * Obtiene un viaje específico por su índice en el cache local.
     * 
     * Este método accede directamente al cache local sin consultar
     * la base de datos, por lo que es muy rápido.
     * 
     * @param index Índice del viaje en la lista local (0-based)
     * @return El objeto Viaje en la posición especificada o null si el índice es inválido
     * 
     * IMPORTANTE: Este método no sincroniza con la base de datos.
     * Asegúrate de que el cache esté actualizado llamando a
     * obtenerTodosLosViajes() o inicializarDesdeBaseDatos() antes.
     */
    public static Viaje obtenerViaje(int index) {
        // Validar que el índice esté dentro del rango válido
        if (index >= 0 && index < viajes.size()) {
            return viajes.get(index);
        }
        return null;
    }
    
    /**
     * Busca viajes que coincidan con el criterio de búsqueda en origen o destino.
     * 
     * Esta búsqueda se realiza sobre el cache local (no consulta la base de datos)
     * y es insensible a mayúsculas/minúsculas. Busca coincidencias parciales
     * tanto en el campo origen como en el destino.
     * 
     * @param busqueda Texto a buscar en origen y destino (puede ser null o vacío)
     * @return Lista de viajes que coinciden con el criterio de búsqueda
     * 
     * Comportamiento:
     * - Si busqueda es null o vacía, retorna lista vacía
     * - Búsqueda insensible a mayúsculas/minúsculas
     * - Busca coincidencias parciales (contains)
     * - Busca en ambos campos: origen Y destino
     * 
     * Ejemplo: buscarViajes("madrid") encontrará viajes con origen="Madrid"
     * o destino="Madrid - Barajas"
     */
    public static List<Viaje> buscarViajes(String busqueda) {
        List<Viaje> resultados = new ArrayList<>();
        
        // Validar parámetro de entrada
        if (busqueda == null || busqueda.trim().isEmpty()) {
            return resultados;
        }
        
        // Normalizar texto de búsqueda (quitar espacios y convertir a minúsculas)
        String busquedaLower = busqueda.toLowerCase().trim();
        
        // Buscar en todos los viajes del cache local
        for (Viaje viaje : viajes) {
            // Verificar coincidencias en origen o destino (insensible a mayúsculas)
            if (viaje.getOrigen().toLowerCase().contains(busquedaLower) ||
                viaje.getDestino().toLowerCase().contains(busquedaLower)) {
                resultados.add(viaje);
            }
        }
        
        return resultados;
    }
    
    
    /**
     * Sincroniza el cache local con los datos actuales de la base de datos.
     * 
     * Este método se llama internamente después de operaciones CRUD para
     * mantener el cache local actualizado. También puede llamarse externamente
     * cuando se necesite refrescar los datos.
     * 
     * Proceso:
     * 1. Consulta todos los viajes desde la base de datos
     * 2. Limpia el cache local completamente
     * 3. Carga los datos frescos en el cache
     * 4. Muestra mensaje informativo con la cantidad de viajes cargados
     * 
     * En caso de error:
     * - El cache local permanece en su estado anterior
     * - Se registra el error en la consola
     * - La aplicación continúa funcionando con los datos en cache
     * 
     * NOTA: Este método no retorna ningún valor. Los errores se manejan
     * internamente para no interrumpir el flujo de la aplicación.
     */
    public static void inicializarDesdeBaseDatos() {
        try {
            // Obtener datos actualizados desde la base de datos
            List<Viaje> viajesDB = viajeService.listAll();
            
            // Reemplazar completamente el cache local
            viajes.clear();
            viajes.addAll(viajesDB);
            
            // Mensaje informativo para monitoreo
            System.out.println("Viajes cargados desde la base de datos: " + viajes.size());
        } catch (Exception e) {
            // Registrar error sin interrumpir la aplicación
            System.err.println("Error al inicializar viajes desde la base de datos: " + e.getMessage());
        }
    }
}
