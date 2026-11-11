package mx.limpieza.modelo;

/**
 * Jefe a cargo de una cuadrilla. Hereda de Persona el id, el nombre
 * y los apellidos.
 */
public class JefeCuadrilla extends Persona {

    public JefeCuadrilla() {
    }

    public JefeCuadrilla(int id, String nombre, String apellidos) {
        super(id, nombre, apellidos);
    }
}
