package mx.limpieza.modelo;

/**
 * Persona que trabaja en una cuadrilla, a cargo de su jefe.
 * Hereda de Persona el id, el nombre y los apellidos.
 */
public class Personal extends Persona {

    private Cuadrilla cuadrilla;

    public Personal() {
    }

    public Personal(int id, String nombre, String apellidos, Cuadrilla cuadrilla) {
        super(id, nombre, apellidos);
        this.cuadrilla = cuadrilla;
    }

    public Cuadrilla getCuadrilla() {
        return cuadrilla;
    }

    public void setCuadrilla(Cuadrilla cuadrilla) {
        this.cuadrilla = cuadrilla;
    }
}
