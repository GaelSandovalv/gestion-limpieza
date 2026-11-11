package mx.limpieza.vista;

import java.awt.Dimension;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import mx.limpieza.modelo.JefeCuadrilla;

/**
 * Pantalla del CRUD de cuadrillas. El jefe se elige en un combo que el
 * controlador llena con los jefes registrados.
 */
public class CuadrillaVista extends PanelCrud {

    private final JTextField txtNombre = new JTextField(20);
    private final JComboBox<JefeCuadrilla> cmbJefe = new JComboBox<>();

    public CuadrillaVista() {
        super("Cuadrillas", new String[] {"ID", "Cuadrilla", "Jefe de cuadrilla"});
        // El combo mide lo mismo que el cuadro de texto aunque haya nombres muy largos
        cmbJefe.setPreferredSize(new Dimension(txtNombre.getPreferredSize().width,
                cmbJefe.getPreferredSize().height));
        setFormulario(crearFormulario(
                new String[] {"Nombre:", "Jefe de cuadrilla:"},
                new JComponent[] {txtNombre, cmbJefe}));
    }

    /** Llena el combo con los jefes registrados. */
    public void setJefes(List<JefeCuadrilla> jefes) {
        cmbJefe.removeAllItems();
        for (JefeCuadrilla jefe : jefes) {
            cmbJefe.addItem(jefe);
        }
    }

    public String getNombre() {
        return txtNombre.getText().trim();
    }

    public void setNombre(String nombre) {
        txtNombre.setText(nombre);
    }

    /** Devuelve el jefe elegido, o null si no hay jefes registrados. */
    public JefeCuadrilla getJefe() {
        return (JefeCuadrilla) cmbJefe.getSelectedItem();
    }

    /**
     * Selecciona en el combo al jefe indicado. Se busca por id porque el jefe
     * que llega de la base de datos es un objeto distinto al del combo.
     */
    public void setJefe(JefeCuadrilla jefe) {
        for (int i = 0; i < cmbJefe.getItemCount(); i++) {
            if (cmbJefe.getItemAt(i).getId() == jefe.getId()) {
                cmbJefe.setSelectedIndex(i);
                return;
            }
        }
    }

    @Override
    public void limpiarFormulario() {
        txtNombre.setText("");
        if (cmbJefe.getItemCount() > 0) {
            cmbJefe.setSelectedIndex(0);
        }
        txtNombre.requestFocusInWindow();
    }
}
