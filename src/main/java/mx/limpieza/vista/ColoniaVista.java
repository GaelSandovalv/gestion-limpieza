package mx.limpieza.vista;

import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.TableColumnModel;

/**
 * Pantalla CRUD del catálogo de colonias de Saltillo (SEPOMEX).
 * Como son cientos de colonias, tiene un cuadro de búsqueda arriba de la tabla.
 */
public class ColoniaVista extends PanelCrud {

    private final JTextField txtCodigoPostal = new JTextField(20);
    private final JTextField txtNombre = new JTextField(30);
    private final JTextField txtTipoAsentamiento = new JTextField(20);
    private final JTextField txtBuscar = new JTextField(25);
    private final JButton btnBuscar = new JButton("Buscar");

    public ColoniaVista() {
        super("Catálogo de colonias (SEPOMEX)",
                new String[] {"ID", "Código postal", "Colonia", "Tipo de asentamiento"});

        setFormulario(crearFormulario(
                new String[] {"Código postal:", "Colonia:", "Tipo de asentamiento:"},
                new JComponent[] {txtCodigoPostal, txtNombre, txtTipoAsentamiento}));

        JPanel barraBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        barraBusqueda.add(new JLabel("Buscar:"));
        barraBusqueda.add(txtBuscar);
        barraBusqueda.add(btnBuscar);
        setBarraSuperior(barraBusqueda);

        // Más espacio para el nombre de la colonia, que es el texto más largo
        TableColumnModel columnas = getTabla().getColumnModel();
        columnas.getColumn(0).setPreferredWidth(50);
        columnas.getColumn(1).setPreferredWidth(100);
        columnas.getColumn(2).setPreferredWidth(400);
        columnas.getColumn(3).setPreferredWidth(180);

        // Con Enter en el cuadro de búsqueda también se presiona el botón Buscar
        txtBuscar.addActionListener(e -> btnBuscar.doClick());
    }

    public String getCodigoPostal() {
        return txtCodigoPostal.getText().trim();
    }

    public void setCodigoPostal(String codigoPostal) {
        txtCodigoPostal.setText(codigoPostal);
    }

    public String getNombre() {
        return txtNombre.getText().trim();
    }

    public void setNombre(String nombre) {
        txtNombre.setText(nombre);
    }

    public String getTipoAsentamiento() {
        return txtTipoAsentamiento.getText().trim();
    }

    public void setTipoAsentamiento(String tipoAsentamiento) {
        txtTipoAsentamiento.setText(tipoAsentamiento);
    }

    public String getTextoBusqueda() {
        return txtBuscar.getText().trim();
    }

    public JButton getBtnBuscar() {
        return btnBuscar;
    }

    @Override
    public void limpiarFormulario() {
        txtCodigoPostal.setText("");
        txtNombre.setText("");
        txtTipoAsentamiento.setText("");
        txtCodigoPostal.requestFocusInWindow();
    }
}
