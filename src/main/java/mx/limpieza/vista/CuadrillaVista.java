package mx.limpieza.vista;

import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 * Pantalla del CRUD de cuadrillas. Solo se captura el nombre: el jefe se
 * marca en la pantalla de personal, y aquí la tabla muestra quién es.
 */
public class CuadrillaVista extends PanelCrud {

    private final JTextField txtNombre = new JTextField(20);

    public CuadrillaVista() {
        super("Cuadrillas", new String[] {"ID", "Cuadrilla", "Jefe de cuadrilla"});
        setFormulario(crearFormulario(
                new String[] {"Nombre:"},
                new JComponent[] {txtNombre}));
    }

    public String getNombre() {
        return txtNombre.getText().trim();
    }

    public void setNombre(String nombre) {
        txtNombre.setText(nombre);
    }

    @Override
    public void limpiarFormulario() {
        txtNombre.setText("");
        txtNombre.requestFocusInWindow();
    }
}
