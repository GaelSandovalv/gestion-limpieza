package mx.limpieza.modelo;

/**
 * Grupo de trabajo que realiza las actividades de limpieza.
 * Su jefe es la persona de su personal marcada como jefe; es null
 * mientras la cuadrilla no tenga jefe.
 */
public class Cuadrilla {

    private int id;
    private String nombre;
    private Personal jefe;

    public Cuadrilla() {
    }

    public Cuadrilla(int id, String nombre, Personal jefe) {
        this.id = id;
        this.nombre = nombre;
        this.jefe = jefe;
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

    public Personal getJefe() {
        return jefe;
    }

    public void setJefe(Personal jefe) {
        this.jefe = jefe;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
