package mx.limpieza.modelo;

/**
 * Usuario que puede entrar al sistema.
 */
public class Administrador {

    private int id;
    private String usuario;
    private String contrasenaHash;
    private String nombre;

    public Administrador() {
    }

    public Administrador(int id, String usuario, String contrasenaHash, String nombre) {
        this.id = id;
        this.usuario = usuario;
        this.contrasenaHash = contrasenaHash;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public void setContrasenaHash(String contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre + " (" + usuario + ")";
    }
}
