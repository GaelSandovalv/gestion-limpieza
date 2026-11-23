package mx.limpieza.modelo;

/**
 * Persona que trabaja en una cuadrilla. Si esJefe es verdadero, es el jefe
 * de cuadrilla y el resto del personal de esa cuadrilla está a su cargo.
 * Hereda de Persona el id, el nombre y los apellidos.
 */
public class Personal extends Persona {

    private boolean esJefe;
    private Cuadrilla cuadrilla;

    public Personal() {
    }

    public Personal(int id, String nombre, String apellidos, boolean esJefe, Cuadrilla cuadrilla) {
        super(id, nombre, apellidos);
        this.esJefe = esJefe;
        this.cuadrilla = cuadrilla;
    }

    public boolean isEsJefe() {
        return esJefe;
    }

    public void setEsJefe(boolean esJefe) {
        this.esJefe = esJefe;
    }

    public Cuadrilla getCuadrilla() {
        return cuadrilla;
    }

    public void setCuadrilla(Cuadrilla cuadrilla) {
        this.cuadrilla = cuadrilla;
    }
}
