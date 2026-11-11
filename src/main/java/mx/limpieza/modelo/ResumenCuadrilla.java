package mx.limpieza.modelo;

/**
 * Una fila del resumen del dashboard: datos de una cuadrilla con su
 * número de personal y de actividades realizadas. Solo se consulta,
 * por eso no tiene setters.
 */
public class ResumenCuadrilla {

    private final int cuadrillaId;
    private final String nombreCuadrilla;
    private final String nombreJefe;
    private final int numPersonal;
    private final int numActividades;

    public ResumenCuadrilla(int cuadrillaId, String nombreCuadrilla, String nombreJefe,
                            int numPersonal, int numActividades) {
        this.cuadrillaId = cuadrillaId;
        this.nombreCuadrilla = nombreCuadrilla;
        this.nombreJefe = nombreJefe;
        this.numPersonal = numPersonal;
        this.numActividades = numActividades;
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

    public int getNumActividades() {
        return numActividades;
    }
}
