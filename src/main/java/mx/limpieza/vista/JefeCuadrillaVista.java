package mx.limpieza.vista;

import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 * Pantalla del CRUD de jefes de cuadrilla.
 */
public class JefeCuadrillaVista extends PanelCrud {

    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtApellidos = new JTextField(20);

    public JefeCuadrillaVista() {
        super("Jefes de cuadrilla", new String[] {"ID", "Nombre", "Apellidos"});
        setFormulario(crearFormulario(
                new String[] {"Nombre:", "Apellidos:"},
                new JComponent[] {txtNombre, txtApellidos}));
    }

    public String getNombre() {
        return txtNombre.getText().trim();
    }

    public void setNombre(String nombre) {
        txtNombre.setText(nombre);
    }

    public String getApellidos() {
        return txtApellidos.getText().trim();
    }

    public void setApellidos(String apellidos) {
        txtApellidos.setText(apellidos);
    }

    @Override
    public void limpiarFormulario() {
        txtNombre.setText("");
        txtApellidos.setText("");
        txtNombre.requestFocusInWindow();
    }
}
