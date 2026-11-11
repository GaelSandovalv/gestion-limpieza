package mx.limpieza.modelo;

/**
 * Datos comunes de las personas del sistema. Es abstracta porque nunca se
 * crea una "persona" sola: siempre es un jefe de cuadrilla o parte del personal.
 */
public abstract class Persona {

    private int id;
    private String nombre;
    private String apellidos;

    protected Persona() {
    }

    protected Persona(int id, String nombre, String apellidos) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
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

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellidos;
    }

    @Override
    public String toString() {
        return getNombreCompleto();
    }
}
