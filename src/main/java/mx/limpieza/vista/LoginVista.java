package mx.limpieza.vista;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Pantalla de acceso para los administradores.
 */
public class LoginVista extends JFrame {

    private final JTextField txtUsuario = new JTextField(18);
    private final JPasswordField txtContrasena = new JPasswordField(18);
    private final JButton btnEntrar = new JButton("Entrar");

    public LoginVista() {
        super("Iniciar sesión");
        // El controlador decide qué hacer al cerrar la ventana
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Gestión de Actividades de Limpieza", SwingConstants.CENTER);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 16f));
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        panel.add(lblTitulo, c);

        c.gridy = 1;
        panel.add(new JLabel("Acceso de administradores", SwingConstants.CENTER), c);

        c.gridwidth = 1;
        c.gridy = 2;
        panel.add(new JLabel("Usuario:"), c);
        c.gridx = 1;
        panel.add(txtUsuario, c);

        c.gridx = 0;
        c.gridy = 3;
        panel.add(new JLabel("Contraseña:"), c);
        c.gridx = 1;
        panel.add(txtContrasena, c);

        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        panel.add(btnEntrar, c);

        // Con Enter también se presiona el botón Entrar
        getRootPane().setDefaultButton(btnEntrar);
        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
    }

    public String getUsuario() {
        return txtUsuario.getText().trim();
    }

    public String getContrasena() {
        return new String(txtContrasena.getPassword());
    }

    public void limpiarContrasena() {
        txtContrasena.setText("");
        txtContrasena.requestFocusInWindow();
    }

    public JButton getBtnEntrar() {
        return btnEntrar;
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
