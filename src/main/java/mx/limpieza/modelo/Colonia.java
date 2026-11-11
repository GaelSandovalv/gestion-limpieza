package mx.limpieza.modelo;

/**
 * Colonia del municipio de Saltillo, tomada del catálogo de SEPOMEX.
 */
public class Colonia {

    private int id;
    private String codigoPostal;
    private String nombre;
    private String tipoAsentamiento;

    public Colonia() {
    }

    public Colonia(int id, String codigoPostal, String nombre, String tipoAsentamiento) {
        this.id = id;
        this.codigoPostal = codigoPostal;
        this.nombre = nombre;
        this.tipoAsentamiento = tipoAsentamiento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoAsentamiento() {
        return tipoAsentamiento;
    }

    public void setTipoAsentamiento(String tipoAsentamiento) {
        this.tipoAsentamiento = tipoAsentamiento;
    }

    @Override
    public String toString() {
        return nombre + " (" + codigoPostal + ")";
    }
}
