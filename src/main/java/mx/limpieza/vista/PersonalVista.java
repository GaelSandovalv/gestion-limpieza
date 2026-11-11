package mx.limpieza.vista;

import java.awt.Dimension;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import mx.limpieza.modelo.Cuadrilla;

/**
 * Pantalla del CRUD del personal. La cuadrilla a la que pertenece cada
 * persona se elige en un combo que el controlador llena.
 */
public class PersonalVista extends PanelCrud {

    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtApellidos = new JTextField(20);
    private final JComboBox<Cuadrilla> cmbCuadrilla = new JComboBox<>();

    public PersonalVista() {
        super("Personal de cuadrilla", new String[] {"ID", "Nombre", "Apellidos", "Cuadrilla"});
        // El combo mide lo mismo que los cuadros de texto aunque haya nombres muy largos
        cmbCuadrilla.setPreferredSize(new Dimension(txtNombre.getPreferredSize().width,
                cmbCuadrilla.getPreferredSize().height));
        setFormulario(crearFormulario(
                new String[] {"Nombre:", "Apellidos:", "Cuadrilla:"},
                new JComponent[] {txtNombre, txtApellidos, cmbCuadrilla}));
    }

    /** Llena el combo con las cuadrillas registradas. */
    public void setCuadrillas(List<Cuadrilla> cuadrillas) {
        cmbCuadrilla.removeAllItems();
        for (Cuadrilla cuadrilla : cuadrillas) {
            cmbCuadrilla.addItem(cuadrilla);
        }
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

    /** Devuelve la cuadrilla elegida, o null si no hay cuadrillas registradas. */
    public Cuadrilla getCuadrilla() {
        return (Cuadrilla) cmbCuadrilla.getSelectedItem();
    }

    /**
     * Selecciona en el combo la cuadrilla indicada. Se busca por id porque la
     * cuadrilla que llega de la base de datos es un objeto distinto al del combo.
     */
    public void setCuadrilla(Cuadrilla cuadrilla) {
        for (int i = 0; i < cmbCuadrilla.getItemCount(); i++) {
            if (cmbCuadrilla.getItemAt(i).getId() == cuadrilla.getId()) {
                cmbCuadrilla.setSelectedIndex(i);
                return;
            }
        }
    }

    @Override
    public void limpiarFormulario() {
        txtNombre.setText("");
        txtApellidos.setText("");
        if (cmbCuadrilla.getItemCount() > 0) {
            cmbCuadrilla.setSelectedIndex(0);
        }
        txtNombre.requestFocusInWindow();
    }
}
