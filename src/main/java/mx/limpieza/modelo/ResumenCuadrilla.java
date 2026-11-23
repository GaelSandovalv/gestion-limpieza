package mx.limpieza.modelo;

/**
 * Una fila del resumen del dashboard: datos de una cuadrilla con su
 * número de personal y de actividades terminadas y pendientes. Solo se
 * consulta, por eso no tiene setters.
 */
public class ResumenCuadrilla {

    private final int cuadrillaId;
    private final String nombreCuadrilla;
    // null si la cuadrilla todavía no tiene jefe
    private final String nombreJefe;
    private final int numPersonal;
    private final int numTerminadas;
    private final int numPendientes;

    public ResumenCuadrilla(int cuadrillaId, String nombreCuadrilla, String nombreJefe,
                            int numPersonal, int numTerminadas, int numPendientes) {
        this.cuadrillaId = cuadrillaId;
        this.nombreCuadrilla = nombreCuadrilla;
        this.nombreJefe = nombreJefe;
        this.numPersonal = numPersonal;
        this.numTerminadas = numTerminadas;
        this.numPendientes = numPendientes;
    }

    public int getCuadrillaId() {
        return cuadrillaId;
    }

    public String getNombreCuadrilla() {
        return nombreCuadrilla;
    }

    public String getNombreJefe() {
        return nombreJefe;
    }

    public int getNumPersonal() {
        return numPersonal;
    }

    public int getNumTerminadas() {
        return numTerminadas;
    }

    public int getNumPendientes() {
        return numPendientes;
    }
}
