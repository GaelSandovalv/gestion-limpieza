package mx.limpieza;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import mx.limpieza.controlador.LoginControlador;
import mx.limpieza.vista.LoginVista;

/**
 * Punto de entrada de la aplicación: abre la pantalla de login.
 */
public class Main {

    public static void main(String[] args) {
        try {
            // Usa la apariencia del sistema operativo
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
            // Si no se puede, se queda la apariencia por defecto de Java
        }
        SwingUtilities.invokeLater(() -> new LoginControlador(new LoginVista()).iniciar());
    }
}
