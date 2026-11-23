package mx.limpieza.controlador;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mx.limpieza.modelo.Administrador;
import mx.limpieza.modelo.dao.AdministradorDAO;
import mx.limpieza.modelo.seguridad.Contrasenas;
import mx.limpieza.modelo.sesion.Sesion;
import mx.limpieza.vista.AdministradorVista;


public class AdministradorControlador {

    private static final int MAXIMO_USUARIO = 50;
    private static final int MAXIMO_NOMBRE = 100;
    
    private static final int ERROR_DUPLICADO = 1062;

    private final AdministradorVista vista;
    private final AdministradorDAO administradorDAO = new AdministradorDAO();
    
    private int idActual = -1;

    public AdministradorControlador(AdministradorVista vista) {
        this.vista = vista;
        vista.getBtnNuevo().addActionListener(e -> nuevo());
        vista.getBtnGuardar().addActionListener(e -> guardar());
        vista.getBtnEliminar().addActionListener(e -> eliminar());
        vista.getTabla().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionar();
            }
        });
        cargarTabla();
    }

    private void cargarTabla() {
        try {
            List<Object[]> filas = new ArrayList<>();
            for (Administrador administrador : administradorDAO.listar()) {
                filas.add(new Object[] {administrador.getId(), administrador.getUsuario(), administrador.getNombre()});
            }
            vista.setFilas(filas);
        } catch (SQLException e) {
            vista.mostrarError(MensajesBD.traducir(e));
        }
    }

    /** Carga en el formulario el administrador seleccionado en la tabla */
    private void seleccionar() {
        int id = vista.getIdSeleccionado();
        if (id == -1) {
            return;
        }
        try {
            Administrador administrador = administradorDAO.buscarPorId(id);
            if (administrador == null) {
                return;
            }
            vista.setUsuario(administrador.getUsuario());
            vista.setNombre(administrador.getNombre());
            vista.limpiarContrasena();
            idActual = id;
        } catch (SQLException e) {
            vista.mostrarError(MensajesBD.traducir(e));
        }
    }

    private void nuevo() {
        vista.limpiarFormulario();
        vista.limpiarSeleccion();
        idActual = -1;
    }

    private void guardar() {
        String usuario = vista.getUsuario().trim();
        String nombre = vista.getNombre().trim();
        String contrasena = vista.getContrasena();
        if (usuario.isEmpty()) {
            vista.mostrarError("Escriba el usuario.");
            return;
        }
        if (usuario.length() > MAXIMO_USUARIO) {
            vista.mostrarError("El usuario no puede tener más de " + MAXIMO_USUARIO + " caracteres.");
            return;
        }
        if (nombre.isEmpty()) {
            vista.mostrarError("Escriba el nombre.");
            return;
        }
        if (nombre.length() > MAXIMO_NOMBRE) {
            vista.mostrarError("El nombre no puede tener más de " + MAXIMO_NOMBRE + " caracteres.");
            return;
        }
        // La contraseña solo es obligatoria al dar de alta
        if (idActual == -1 && contrasena.isBlank()) {
            vista.mostrarError("Escriba la contraseña.");
            return;
        }
        try {
            if (idActual == -1) {
                Administrador administrador = new Administrador(0, usuario, Contrasenas.generarHash(contrasena), nombre);
                administradorDAO.insertar(administrador);
            } else {
               
                Administrador administrador = administradorDAO.buscarPorId(idActual);
                administrador.setUsuario(usuario);
                administrador.setNombre(nombre);
                if (!contrasena.isBlank()) {
                    administrador.setContrasenaHash(Contrasenas.generarHash(contrasena));
                }
                administradorDAO.actualizar(administrador);
              
                if (esAdministradorDeLaSesion(idActual)) {
                    Sesion.getInstancia().iniciar(administrador);
                }
            }
            cargarTabla();
            nuevo();
            vista.mostrarMensaje("Administrador guardado correctamente.");
        } catch (SQLException e) {
            if (e.getErrorCode() == ERROR_DUPLICADO) {
                vista.mostrarError("Ese nombre de usuario ya existe.");
            } else {
                vista.mostrarError(MensajesBD.traducir(e));
            }
        }
    }

    private void eliminar() {
        int id = vista.getIdSeleccionado();
        if (id == -1) {
            vista.mostrarError("Seleccione el administrador que desea eliminar.");
            return;
        }
        if (esAdministradorDeLaSesion(id)) {
            vista.mostrarError("No puede eliminar su propio usuario mientras tiene la sesión iniciada.");
            return;
        }
        if (!vista.confirmar("¿Desea eliminar al administrador seleccionado?")) {
            return;
        }
        try {
            administradorDAO.eliminar(id);
            cargarTabla();
            nuevo();
            vista.mostrarMensaje("Administrador eliminado.");
        } catch (SQLException e) {
            vista.mostrarError(MensajesBD.traducir(e));
        }
    }

    private boolean esAdministradorDeLaSesion(int id) {
        return id == Sesion.getInstancia().getAdministrador().getId();
    }
}
