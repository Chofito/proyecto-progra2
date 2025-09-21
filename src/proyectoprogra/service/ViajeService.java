package proyectoprogra.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import proyectoprogra.OracleConnector;
import proyectoprogra.model.Viaje;

public class ViajeService {

    public Viaje create(Viaje v) {
        validate(v);

        String sql = "INSERT INTO VIAJE (origen, destino, fecha_salida, fecha_llegada, estado) VALUES (?,?,?,?,?)";

        try (Connection con = OracleConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, v.getOrigen());
            ps.setString(2, v.getDestino());
            ps.setTimestamp(3, new Timestamp(v.getFechaSalida().getTime()));
            ps.setTimestamp(4, new Timestamp(v.getFechaLlegada().getTime()));
            ps.setString(5, v.getEstado());

            int affected = ps.executeUpdate();
            
            if (affected == 0) {
                throw new SQLException("No se insertó ningún registro de Viaje");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    return getById(generatedId);
                } else {
                    throw new SQLException("No se obtuvo ID generado para Viaje");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al crear Viaje", e);
        }
    }

    public Viaje getById(int id) {
        String sql = "SELECT id, origen, destino, fecha_salida, fecha_llegada, estado FROM VIAJE WHERE id = ?";

        try (Connection con = OracleConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener Viaje por id=" + id, e);
        }
    }

    public List<Viaje> listAll() {
        String sql = "SELECT id, origen, destino, fecha_salida, fecha_llegada, estado FROM VIAJE ORDER BY id";

        List<Viaje> list = new ArrayList<>();

        try (Connection con = OracleConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar Viajes", e);
        }

        return list;
    }

    public Viaje update(Viaje v) {
        if (v.getId() <= 0) {
            throw new IllegalArgumentException("id inválido para update");
        }

        validate(v);

        String sql = "UPDATE VIAJE SET origen=?, destino=?, fecha_salida=?, fecha_llegada=?, estado=? WHERE id=?";

        try (Connection con = OracleConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, v.getOrigen());
            ps.setString(2, v.getDestino());
            ps.setTimestamp(3, new Timestamp(v.getFechaSalida().getTime()));
            ps.setTimestamp(4, new Timestamp(v.getFechaLlegada().getTime()));
            ps.setString(5, v.getEstado());
            ps.setInt(6, v.getId());

            int affected = ps.executeUpdate();

            if (affected == 0) {
                throw new NoSuchElementException("No existe viaje con id=" + v.getId());
            }

            return getById(v.getId());

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar Viaje id=" + v.getId(), e);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM VIAJE WHERE id=?";

        try (Connection con = OracleConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar Viaje id=" + id, e);
        }
    }

    private void validate(Viaje v) {
        if (v == null) {
            throw new IllegalArgumentException("Viaje no puede ser null");
        }

        if (v.getOrigen() == null || v.getOrigen().isBlank()) {
            throw new IllegalArgumentException("Origen es requerido");
        }

        if (v.getDestino() == null || v.getDestino().isBlank()) {
            throw new IllegalArgumentException("Destino es requerido");
        }

        if (v.getFechaSalida() == null) {
            throw new IllegalArgumentException("fechaSalida es requerida");
        }

        if (v.getFechaLlegada() == null) {
            throw new IllegalArgumentException("fechaLlegada es requerida");
        }

        if (v.getFechaLlegada().before(v.getFechaSalida())) {
            throw new IllegalArgumentException("fechaLlegada no puede ser antes de fechaSalida");
        }

        String estado = v.getEstado();

        if (!Viaje.ESTADO_EN_CURSO.equals(estado) && !Viaje.ESTADO_PENDIENTE.equals(estado)) {
            throw new IllegalArgumentException("estado inválido: " + estado + ". Debe ser '" + Viaje.ESTADO_EN_CURSO
                    + "' o '" + Viaje.ESTADO_PENDIENTE + "'");
        }
    }

    private Viaje mapRow(ResultSet rs) throws SQLException {
        Viaje v = new Viaje();

        v.setId(rs.getInt("id"));
        v.setOrigen(rs.getString("origen"));
        v.setDestino(rs.getString("destino"));

        Timestamp tsSalida = rs.getTimestamp("fecha_salida");
        Timestamp tsLlegada = rs.getTimestamp("fecha_llegada");

        v.setFechaSalida(tsSalida == null ? null : new java.util.Date(tsSalida.getTime()));
        v.setFechaLlegada(tsLlegada == null ? null : new java.util.Date(tsLlegada.getTime()));
        v.setEstado(rs.getString("estado"));

        return v;
    }
}
