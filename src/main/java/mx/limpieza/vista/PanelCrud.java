package mx.limpieza.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * Pantalla base para los CRUD: título, formulario con los botones
 * Nuevo, Guardar y Eliminar a la izquierda, y la tabla de registros al centro.
 * Cada pantalla CRUD hereda de esta clase y solo arma su formulario.
 * La primera columna de la tabla siempre es el ID.
 */
public abstract class PanelCrud extends JPanel {

    private final DefaultTableModel modeloTabla;
    private final JTable tabla;
    private final JButton btnNuevo = new JButton("Nuevo");
    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JPanel contenedorFormulario = new JPanel(new BorderLayout());
    private final JPanel centro = new JPanel(new BorderLayout(0, 5));

    protected PanelCrud(String titulo, String[] columnas) {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 18f));
        add(lblTitulo, BorderLayout.NORTH);

        // La tabla solo es de consulta: no se edita directamente
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnEliminar);

        JPanel izquierda = new JPanel(new BorderLayout(0, 10));
        izquierda.setBorder(BorderFactory.createTitledBorder("Datos"));
        izquierda.add(contenedorFormulario, BorderLayout.NORTH);
        izquierda.add(botones, BorderLayout.SOUTH);
        add(izquierda, BorderLayout.WEST);
    }

    /** Coloca el formulario propio de cada pantalla. */
    protected void setFormulario(JPanel formulario) {
        contenedorFormulario.removeAll();
        contenedorFormulario.add(formulario, BorderLayout.CENTER);
        revalidate();
    }

    /** Coloca una barra arriba de la tabla (por ejemplo, para buscar). */
    protected void setBarraSuperior(JComponent barra) {
        centro.add(barra, BorderLayout.NORTH);
        revalidate();
    }

    /** Arma un formulario sencillo con una etiqueta por cada campo. */
    protected static JPanel crearFormulario(String[] etiquetas, JComponent[] campos) {
        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        for (int i = 0; i < etiquetas.length; i++) {
            c.gridx = 0;
            c.gridy = i;
            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            formulario.add(new JLabel(etiquetas[i]), c);
            c.gridx = 1;
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            formulario.add(campos[i], c);
        }
        return formulario;
    }

    /** Reemplaza el contenido de la tabla. */
    public void setFilas(List<Object[]> filas) {
        modeloTabla.setRowCount(0);
        for (Object[] fila : filas) {
            modeloTabla.addRow(fila);
        }
    }

    /** Devuelve el ID del registro seleccionado, o -1 si no hay selección. */
    public int getIdSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return -1;
        }
        Object valor = modeloTabla.getValueAt(tabla.convertRowIndexToModel(fila), 0);
        return ((Number) valor).intValue();
    }

    public void limpiarSeleccion() {
        tabla.clearSelection();
    }

    public JTable getTabla() {
        return tabla;
    }

    public JButton getBtnNuevo() {
        return btnNuevo;
    }

    public JButton getBtnGuardar() {
        return btnGuardar;
    }

    public JButton getBtnEliminar() {
        return btnEliminar;
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean confirmar(String mensaje) {
        return JOptionPane.showConfirmDialog(this, mensaje, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    /** Deja el formulario vacío para capturar un registro nuevo. */
    public abstract void limpiarFormulario();
}
