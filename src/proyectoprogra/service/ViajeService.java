package proyectoprogra.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import proyectoprogra.database.OracleConnector;
import proyectoprogra.model.Viaje;

/**
 * Servicio para manejar las operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * de la entidad Viaje en la base de datos Oracle.
 * 
 * Esta clase implementa el patrón DAO (Data Access Object) y maneja todas
 * las operaciones de persistencia relacionadas con los viajes.
 */
public class ViajeService {

    /**
     * Crea un nuevo viaje en la base de datos.
     * 
     * @param v El objeto Viaje a crear (sin ID, se genera automáticamente)
     * @return El objeto Viaje creado con el ID generado por la base de datos
     * @throws RuntimeException si ocurre un error durante la inserción
     * @throws IllegalArgumentException si los datos del viaje no son válidos
     */
    public Viaje create(Viaje v) {
        // Validar los datos del viaje antes de insertarlo
        validate(v);

        // SQL para insertar un nuevo viaje con parámetros preparados (previene SQL injection)
        String sql = "INSERT INTO VIAJE (origen, destino, fecha_salida, fecha_llegada, estado) VALUES (?,?,?,?,?)";

        // Try-with-resources para manejo automático de recursos
        try (Connection con = OracleConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, new String[]{"ID"}))
        {

            // Establecer los parámetros del PreparedStatement
            ps.setString(1, v.getOrigen());
            ps.setString(2, v.getDestino());
            // Convertir java.util.Date a java.sql.Timestamp para la base de datos
            ps.setTimestamp(3, new Timestamp(v.getFechaSalida().getTime()));
            ps.setTimestamp(4, new Timestamp(v.getFechaLlegada().getTime()));
            ps.setString(5, v.getEstado());

            // Ejecutar la inserción
            int affected = ps.executeUpdate();
            
            // Verificar que se insertó al menos un registro
            if (affected == 0) {
                throw new SQLException("No se insertó ningún registro de Viaje");
            }

            // Obtener el ID generado automáticamente por la base de datos
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    // Retornar el viaje completo con el ID generado
                    return getById(generatedId);
                } else {
                    throw new SQLException("No se obtuvo ID generado para Viaje");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al crear Viaje", e);
        }
    }

    /**
     * Busca un viaje por su ID único.
     * 
     * @param id El ID del viaje a buscar
     * @return El objeto Viaje encontrado o null si no existe
     * @throws RuntimeException si ocurre un error durante la consulta
     */
    public Viaje getById(int id) {
        // SQL para buscar un viaje específico por ID
        String sql = "SELECT id, origen, destino, fecha_salida, fecha_llegada, estado FROM VIAJE WHERE id = ?";

        try (Connection con = OracleConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Convertir el ResultSet a objeto Viaje
                    return mapRow(rs);
                }
                // Retornar null si no se encuentra el viaje
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener Viaje por id=" + id, e);
        }
    }

    /**
     * Obtiene todos los viajes de la base de datos ordenados por ID.
     * 
     * @return Lista de todos los viajes en la base de datos
     * @throws RuntimeException si ocurre un error durante la consulta
     */
    public List<Viaje> listAll() {
        // SQL para obtener todos los viajes ordenados por ID
        String sql = "SELECT id, origen, destino, fecha_salida, fecha_llegada, estado FROM VIAJE ORDER BY id";

        List<Viaje> list = new ArrayList<>();

        try (Connection con = OracleConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Iterar por todos los resultados y convertirlos a objetos Viaje
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar Viajes", e);
        }

        return list;
    }

    /**
     * Actualiza un viaje existente en la base de datos.
     * 
     * @param v El objeto Viaje con los datos actualizados (debe tener un ID válido)
     * @return El objeto Viaje actualizado
     * @throws IllegalArgumentException si el ID es inválido o los datos no son válidos
     * @throws NoSuchElementException si el viaje no existe
     * @throws RuntimeException si ocurre un error durante la actualización
     */
    public Viaje update(Viaje v) {
        // Verificar que el viaje tenga un ID válido para actualizar
        if (v.getId() <= 0) {
            throw new IllegalArgumentException("id inválido para update");
        }

        // Validar los datos del viaje
        validate(v);

        // SQL para actualizar un viaje existente
        String sql = "UPDATE VIAJE SET origen=?, destino=?, fecha_salida=?, fecha_llegada=?, estado=? WHERE id=?";

        try (Connection con = OracleConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Establecer los parámetros para la actualización
            ps.setString(1, v.getOrigen());
            ps.setString(2, v.getDestino());
            ps.setTimestamp(3, new Timestamp(v.getFechaSalida().getTime()));
            ps.setTimestamp(4, new Timestamp(v.getFechaLlegada().getTime()));
            ps.setString(5, v.getEstado());
            ps.setInt(6, v.getId()); // ID para la cláusula WHERE

            int affected = ps.executeUpdate();

            // Verificar que se actualizó al menos un registro
            if (affected == 0) {
                throw new NoSuchElementException("No existe viaje con id=" + v.getId());
            }

            // Retornar el viaje actualizado desde la base de datos
            return getById(v.getId());

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar Viaje id=" + v.getId(), e);
        }
    }

    /**
     * Elimina un viaje de la base de datos por su ID.
     * 
     * @param id El ID del viaje a eliminar
     * @return true si se eliminó el viaje, false si no existía
     * @throws RuntimeException si ocurre un error durante la eliminación
     */
    public boolean delete(int id) {
        // SQL para eliminar un viaje por ID
        String sql = "DELETE FROM VIAJE WHERE id=?";

        try (Connection con = OracleConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            // Retornar true si se eliminó al menos un registro
            return affected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar Viaje id=" + id, e);
        }
    }

    /**
     * Valida que los datos de un viaje sean correctos antes de guardarlo.
     * 
     * @param v El objeto Viaje a validar
     * @throws IllegalArgumentException si algún dato es inválido
     */
    private void validate(Viaje v) {
        // Verificar que el objeto no sea null
        if (v == null) {
            throw new IllegalArgumentException("Viaje no puede ser null");
        }

        // Validar que el origen no esté vacío
        if (v.getOrigen() == null || v.getOrigen().isBlank()) {
            throw new IllegalArgumentException("Origen es requerido");
        }

        // Validar que el destino no esté vacío
        if (v.getDestino() == null || v.getDestino().isBlank()) {
            throw new IllegalArgumentException("Destino es requerido");
        }

        // Validar que las fechas estén presentes
        if (v.getFechaSalida() == null) {
            throw new IllegalArgumentException("fechaSalida es requerida");
        }

        if (v.getFechaLlegada() == null) {
            throw new IllegalArgumentException("fechaLlegada es requerida");
        }

        // Validar que la fecha de llegada sea posterior a la de salida
        if (v.getFechaLlegada().before(v.getFechaSalida())) {
            throw new IllegalArgumentException("fechaLlegada no puede ser antes de fechaSalida");
        }

        String estado = v.getEstado();

        // Validar que el estado sea uno de los valores permitidos
        if (!Viaje.ESTADO_EN_CURSO.equals(estado) && !Viaje.ESTADO_PENDIENTE.equals(estado)) {
            throw new IllegalArgumentException("estado inválido: " + estado + ". Debe ser '" + Viaje.ESTADO_EN_CURSO
                    + "' o '" + Viaje.ESTADO_PENDIENTE + "'");
        }
    }

    /**
     * Método auxiliar para convertir una fila del ResultSet en un objeto Viaje.
     * Este patrón se conoce como "Row Mapper" y evita duplicación de código.
     * 
     * @param rs El ResultSet posicionado en la fila a convertir
     * @return Un objeto Viaje con los datos de la fila
     * @throws SQLException si ocurre un error al leer los datos
     */
    private Viaje mapRow(ResultSet rs) throws SQLException {
        Viaje v = new Viaje();

        // Mapear los campos básicos
        v.setId(rs.getInt("id"));
        v.setOrigen(rs.getString("origen"));
        v.setDestino(rs.getString("destino"));

        // Obtener los timestamps de la base de datos
        Timestamp tsSalida = rs.getTimestamp("fecha_salida");
        Timestamp tsLlegada = rs.getTimestamp("fecha_llegada");

        // Convertir Timestamp a java.util.Date (manejar valores null)
        v.setFechaSalida(tsSalida == null ? null : new java.util.Date(tsSalida.getTime()));
        v.setFechaLlegada(tsLlegada == null ? null : new java.util.Date(tsLlegada.getTime()));
        v.setEstado(rs.getString("estado"));

        return v;
    }
}
