package mx.limpieza.controlador;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import mx.limpieza.modelo.Administrador;
import mx.limpieza.modelo.conexion.ConexionBD;
import mx.limpieza.modelo.dao.AdministradorDAO;
import mx.limpieza.modelo.seguridad.Contrasenas;
import mx.limpieza.modelo.sesion.Sesion;
import mx.limpieza.vista.LoginVista;
import mx.limpieza.vista.VentanaPrincipal;

/**
 * Valida el usuario y la contraseña del administrador y, si son correctos,
 * inicia la sesión y abre la ventana principal.
 */
public class LoginControlador {

    private final LoginVista vista;
    private final AdministradorDAO administradorDAO = new AdministradorDAO();

    public LoginControlador(LoginVista vista) {
        this.vista = vista;
        vista.getBtnEntrar().addActionListener(e -> iniciarSesion());
        vista.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salir();
            }
        });
    }

    public void iniciar() {
        vista.setVisible(true);
    }

    private void iniciarSesion() {
        String usuario = vista.getUsuario();
        String contrasena = vista.getContrasena();
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            vista.mostrarError("Escriba el usuario y la contraseña.");
            return;
        }
        try {
            Administrador administrador = administradorDAO.buscarPorUsuario(usuario);
            if (administrador == null || !Contrasenas.verificar(contrasena, administrador.getContrasenaHash())) {
                vista.mostrarError("Usuario o contraseña incorrectos.");
                vista.limpiarContrasena();
                return;
            }
            Sesion.getInstancia().iniciar(administrador);
            vista.dispose();
            new PrincipalControlador(new VentanaPrincipal()).iniciar();
        } catch (SQLException e) {
            vista.mostrarError(MensajesBD.traducir(e));
        }
    }

    private void salir() {
        ConexionBD.cerrar();
        vista.dispose();
        System.exit(0);
    }
}
