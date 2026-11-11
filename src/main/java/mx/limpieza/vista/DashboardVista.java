package mx.limpieza.vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import mx.limpieza.modelo.Colonia;

/**
 * Dashboard ejecutivo: arriba, el total de actividades, la tabla resumen por
 * cuadrilla y la gráfica de barras; abajo, la consulta de las actividades de
 * la cuadrilla seleccionada y el detalle de la actividad seleccionada.
 */
public class DashboardVista extends JPanel {

    private static final String SIN_IMAGEN = "Sin imagen";
    // Tamaño máximo de la vista previa de la evidencia
    private static final int ANCHO_IMAGEN = 200;
    private static final int ALTO_IMAGEN = 200;

    private final JLabel lblTotal = new JLabel();
    private final JTable tablaResumen = crearTabla(
            new String[] {"ID", "Cuadrilla", "Jefe de cuadrilla", "Personal", "Actividades realizadas"});
    private final GraficaBarras grafica = new GraficaBarras();
    private final JTable tablaActividades = crearTabla(new String[] {"ID", "Actividad", "Fecha", "Lugar"});
    private final JTextArea txtDetalle = new JTextArea();
    private final DefaultListModel<Colonia> modeloColonias = new DefaultListModel<>();
    private final JList<Colonia> lstColonias = new JList<>(modeloColonias);
    private final JLabel lblImagen = new JLabel(SIN_IMAGEN, SwingConstants.CENTER);

    public DashboardVista() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Encabezado: título y total general de actividades
        JLabel lblTitulo = new JLabel("Dashboard ejecutivo por cuadrilla");
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 18f));
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 14f));
        setTotalActividades(0);
        JPanel encabezado = new JPanel(new GridLayout(2, 1, 0, 4));
        encabezado.add(lblTitulo);
        encabezado.add(lblTotal);
        add(encabezado, BorderLayout.NORTH);

        // Parte de arriba: la tabla resumen junto a la gráfica
        anchoColumnas(tablaResumen, 40, 160, 180, 70, 140);
        JPanel resumen = new JPanel(new GridLayout(1, 2, 10, 0));
        resumen.add(enMarco("Resumen por cuadrilla", new JScrollPane(tablaResumen)));
        resumen.add(enMarco("Gráfica", grafica));

        // Parte de abajo: actividades de la cuadrilla y detalle de la actividad
        anchoColumnas(tablaActividades, 40, 170, 90, 200);
        txtDetalle.setEditable(false);
        txtDetalle.setLineWrap(true);
        txtDetalle.setWrapStyleWord(true);
        // Misma letra que las etiquetas (un JTextArea trae por defecto una letra monoespaciada)
        txtDetalle.setFont(UIManager.getFont("Label.font"));

        JPanel detalle = new JPanel(new GridLayout(1, 3, 10, 0));
        detalle.add(enMarco("Detalle", new JScrollPane(txtDetalle)));
        detalle.add(enMarco("Colonias atendidas", new JScrollPane(lstColonias)));
        detalle.add(enMarco("Evidencia", lblImagen));
        // Ancho fijo para el detalle; la tabla de actividades toma el resto
        detalle.setPreferredSize(new Dimension(690, 0));

        JPanel consulta = new JPanel(new BorderLayout(10, 0));
        consulta.add(enMarco("Actividades de la cuadrilla seleccionada", new JScrollPane(tablaActividades)),
                BorderLayout.CENTER);
        consulta.add(detalle, BorderLayout.EAST);

        // Las dos partes se reparten la altura por mitad
        JPanel centro = new JPanel(new GridLayout(2, 1, 0, 10));
        centro.add(resumen);
        centro.add(consulta);
        add(centro, BorderLayout.CENTER);
    }

    /** Crea una tabla de solo consulta (no editable) que permite seleccionar una fila. */
    private static JTable crearTabla(String[] columnas) {
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        JTable tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        return tabla;
    }

    /** Ancho inicial de cada columna, en píxeles. */
    private static void anchoColumnas(JTable tabla, int... anchos) {
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    /** Pone el componente dentro de un recuadro con título. */
    private static JPanel enMarco(String titulo, JComponent componente) {
        JPanel marco = new JPanel(new BorderLayout());
        marco.setBorder(BorderFactory.createTitledBorder(titulo));
        marco.add(componente, BorderLayout.CENTER);
        return marco;
    }

    private static void llenarTabla(JTable tabla, List<Object[]> filas) {
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0);
        for (Object[] fila : filas) {
            modelo.addRow(fila);
        }
    }

    /** Devuelve el ID (columna 0) de la fila seleccionada, o -1 si no hay selección. */
    private static int idSeleccionado(JTable tabla) {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return -1;
        }
        return ((Number) tabla.getModel().getValueAt(tabla.convertRowIndexToModel(fila), 0)).intValue();
    }

    public void setTotalActividades(int total) {
        lblTotal.setText("Total de actividades realizadas: " + total);
    }

    public void setResumen(List<Object[]> filas) {
        llenarTabla(tablaResumen, filas);
    }

    public void setDatosGrafica(List<String> etiquetas, List<Integer> valores) {
        grafica.setDatos(etiquetas, valores);
    }

    public void setActividades(List<Object[]> filas) {
        llenarTabla(tablaActividades, filas);
    }

    public void setDetalle(String detalle) {
        txtDetalle.setText(detalle);
        // Muestra el texto desde el principio
        txtDetalle.setCaretPosition(0);
    }

    public void setColonias(List<Colonia> colonias) {
        modeloColonias.clear();
        for (Colonia colonia : colonias) {
            modeloColonias.addElement(colonia);
        }
    }

    /** Muestra la evidencia reducida para que quepa (sin agrandarla), o "Sin imagen". */
    public void setImagen(byte[] imagen) {
        ImageIcon icono = (imagen != null) ? new ImageIcon(imagen) : null;
        if (icono == null || icono.getIconWidth() <= 0) {
            lblImagen.setIcon(null);
            lblImagen.setText(SIN_IMAGEN);
            return;
        }
        double escala = Math.min(1.0, Math.min(
                (double) ANCHO_IMAGEN / icono.getIconWidth(),
                (double) ALTO_IMAGEN / icono.getIconHeight()));
        int ancho = Math.max(1, (int) Math.round(icono.getIconWidth() * escala));
        int alto = Math.max(1, (int) Math.round(icono.getIconHeight() * escala));
        lblImagen.setIcon(new ImageIcon(icono.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH)));
        lblImagen.setText(null);
    }

    /** Deja vacío el detalle, la lista de colonias y la imagen. */
    public void limpiarDetalle() {
        txtDetalle.setText("");
        modeloColonias.clear();
        setImagen(null);
    }

    public int getIdCuadrillaSeleccionada() {
        return idSeleccionado(tablaResumen);
    }

    public int getIdActividadSeleccionada() {
        return idSeleccionado(tablaActividades);
    }

    public JTable getTablaResumen() {
        return tablaResumen;
    }

    public JTable getTablaActividades() {
        return tablaActividades;
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
