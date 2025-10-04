package proyectoprogra.model;

import java.util.Date;
import java.util.Objects;

/**
 * Modelo que representa un viaje en el sistema.
 * Contiene toda la información de un viaje: origen, destino, fechas y estado.
 */
public class Viaje {
    // Identificador único del viaje
    private int id;
    // Ciudad de origen
    private String origen;
    // Ciudad de destino
    private String destino;
    // Fecha y hora de salida
    private Date fechaSalida;
    // Fecha y hora de llegada
    private Date fechaLlegada;
    // Estado actual del viaje
    private String estado;

    // Estados posibles de un viaje
    public static final String ESTADO_EN_CURSO = "En curso";
    public static final String ESTADO_PENDIENTE = "Pendiente";

    // Constructor vacío
    public Viaje() { }

    // Constructor completo
    public Viaje(int id, String origen, String destino, Date fechaSalida, Date fechaLlegada, String estado) {
        this.id = id;
        this.origen = origen;
        this.destino = destino;
        this.fechaSalida = fechaSalida;
        this.fechaLlegada = fechaLlegada;
        this.estado = estado;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public Date getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(Date fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public Date getFechaLlegada() {
        return fechaLlegada;
    }

    public void setFechaLlegada(Date fechaLlegada) {
        this.fechaLlegada = fechaLlegada;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // Métodos Object
    
    // Dos viajes son iguales si tienen el mismo ID
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Viaje viaje = (Viaje) o;

        return id == viaje.id;
    }

    // Hash code basado en el ID
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Representación en texto del viaje
    @Override
    public String toString() {
        return "Viaje{" +
                "id=" + id +
                ", origen='" + origen + '\'' +
                ", destino='" + destino + '\'' +
                ", fechaSalida=" + fechaSalida +
                ", fechaLlegada=" + fechaLlegada +
                ", estado='" + estado + '\'' +
                '}';
    }
}
