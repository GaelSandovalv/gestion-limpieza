package mx.limpieza.modelo;

/**
 * Grupo de trabajo que realiza las actividades de limpieza.
 * Tiene un solo jefe, y un jefe dirige una sola cuadrilla.
 */
public class Cuadrilla {

    private int id;
    private String nombre;
    private JefeCuadrilla jefe;

    public Cuadrilla() {
    }

    public Cuadrilla(int id, String nombre, JefeCuadrilla jefe) {
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

    public JefeCuadrilla getJefe() {
        return jefe;
    }

    public void setJefe(JefeCuadrilla jefe) {
        this.jefe = jefe;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
