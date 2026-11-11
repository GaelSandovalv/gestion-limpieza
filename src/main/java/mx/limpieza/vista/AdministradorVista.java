package mx.limpieza.vista;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Pantalla del CRUD de administradores. La tabla muestra el usuario y el
 * nombre; la contraseña nunca se muestra.
 */
public class AdministradorVista extends PanelCrud {

    private final JTextField txtUsuario = new JTextField(20);
    private final JTextField txtNombre = new JTextField(20);
    private final JPasswordField txtContrasena = new JPasswordField(20);

    public AdministradorVista() {
        super("Administradores", new String[] {"ID", "Usuario", "Nombre"});

        JLabel lblNota = new JLabel("Al modificar, deje la contraseña vacía para conservar la actual.");
        lblNota.setFont(lblNota.getFont().deriveFont(Font.ITALIC, 11f));
        lblNota.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));

        JPanel formulario = new JPanel(new BorderLayout(0, 4));
        formulario.add(crearFormulario(
                new String[] {"Usuario:", "Nombre:", "Contraseña:"},
                new JComponent[] {txtUsuario, txtNombre, txtContrasena}), BorderLayout.CENTER);
        formulario.add(lblNota, BorderLayout.SOUTH);
        setFormulario(formulario);
    }

    @Override
    public void limpiarFormulario() {
        txtUsuario.setText("");
        txtNombre.setText("");
        txtContrasena.setText("");
        txtUsuario.requestFocusInWindow();
    }

    public String getUsuario() {
        return txtUsuario.getText();
    }

    public void setUsuario(String usuario) {
        txtUsuario.setText(usuario);
    }

    public String getNombre() {
        return txtNombre.getText();
    }

    public void setNombre(String nombre) {
        txtNombre.setText(nombre);
    }

    public String getContrasena() {
        return new String(txtContrasena.getPassword());
    }

    public void limpiarContrasena() {
        txtContrasena.setText("");
    }
}
