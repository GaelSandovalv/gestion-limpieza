package mx.limpieza.modelo.sesion;

import mx.limpieza.modelo.Administrador;


public final class Sesion {

    // Única instancia de la clase
    private static Sesion instancia;

    private Administrador administrador;

    private Sesion() {
    }

    public static synchronized Sesion getInstancia() {
        if (instancia == null) {
            instancia = new Sesion();
        }
        return instancia;
    }

    public void iniciar(Administrador administrador) {
        this.administrador = administrador;
    }

    public Administrador getAdministrador() {
        return administrador;
    }

    public boolean estaActiva() {
        return administrador != null;
    }

    /**
     * Destruye la sesión: borra al administrador y elimina la instancia.
     */
    public static synchronized void destruir() {
        if (instancia != null) {
            instancia.administrador = null;
            instancia = null;
        }
    }
}
