package mx.limpieza.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Actividad de limpieza que una cuadrilla realizó en un lugar específico.
 * Guarda las colonias que se atendieron y, si se cargó, la imagen de evidencia.
 */
public class Actividad {

    private int id;
    private String nombre;
    private String detalle;
    private LocalDate fecha;
    private String lugar;
    private Cuadrilla cuadrilla;
    private List<Colonia> colonias = new ArrayList<>();
    // Imagen de evidencia en bytes (JPEG); es null si no se cargó ninguna
    private byte[] evidencia;

    public Actividad() {
    }

    public Actividad(int id, String nombre, String detalle, LocalDate fecha, String lugar, Cuadrilla cuadrilla) {
        this.id = id;
        this.nombre = nombre;
        this.detalle = detalle;
        this.fecha = fecha;
        this.lugar = lugar;
        this.cuadrilla = cuadrilla;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public Cuadrilla getCuadrilla() {
        return cuadrilla;
    }

    public void setCuadrilla(Cuadrilla cuadrilla) {
        this.cuadrilla = cuadrilla;
    }

    public List<Colonia> getColonias() {
        return colonias;
    }

    public void setColonias(List<Colonia> colonias) {
        // La lista nunca queda en null: si no hay colonias, queda vacía
        this.colonias = (colonias != null) ? colonias : new ArrayList<>();
    }

    public byte[] getEvidencia() {
        return evidencia;
    }

    public void setEvidencia(byte[] evidencia) {
        this.evidencia = evidencia;
    }
}
