package mx.limpieza.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumnModel;
import mx.limpieza.modelo.Colonia;
import mx.limpieza.modelo.Cuadrilla;

/**
 * Pantalla del CRUD de actividades de limpieza. Además de los datos de la
 * actividad, permite buscar y agregar las colonias atendidas y cargar la
 * imagen de evidencia con una vista previa.
 */
public class ActividadVista extends PanelCrud {

    // Tamaño del recuadro de la vista previa de la imagen
    private static final int ANCHO_IMAGEN = 220;
    private static final int ALTO_IMAGEN = 160;

    // Tamaño de las listas de colonias
    private static final int ANCHO_LISTA = 260;
    private static final int ALTO_LISTA = 120;

    private final JTextField txtNombre = new JTextField(20);
    private final JTextArea txtDetalle = new JTextArea(4, 20);
    private final JSpinner spnFecha = new JSpinner(new SpinnerDateModel());
    private final JTextField txtLugar = new JTextField(20);
    private final JComboBox<Cuadrilla> cmbCuadrilla = new JComboBox<>();
    private final JLabel lblImagen = new JLabel("Sin imagen", SwingConstants.CENTER);
    private final JButton btnCargarImagen = new JButton("Cargar imagen");

    private final JTextField txtBuscarColonia = new JTextField(14);
    private final JButton btnBuscar = new JButton("Buscar");
    private final DefaultListModel<Colonia> modeloResultados = new DefaultListModel<>();
    private final JList<Colonia> lstResultados = new JList<>(modeloResultados);
    private final JButton btnAgregar = new JButton("Agregar");
    private final DefaultListModel<Colonia> modeloColonias = new DefaultListModel<>();
    private final JList<Colonia> lstColonias = new JList<>(modeloColonias);
    private final JButton btnQuitar = new JButton("Quitar");

    public ActividadVista() {
        super("Actividades de limpieza", new String[]{"ID", "Actividad", "Fecha", "Lugar", "Cuadrilla"});

        // El área de texto usa la misma letra que los demás campos
        txtDetalle.setFont(txtNombre.getFont());
        txtDetalle.setLineWrap(true);
        txtDetalle.setWrapStyleWord(true);
        spnFecha.setEditor(new JSpinner.DateEditor(spnFecha, "dd/MM/yyyy"));
        // El combo no se ensancha si una cuadrilla tiene un nombre muy largo
        cmbCuadrilla.setPrototypeDisplayValue(new Cuadrilla(0, "Nombre de la cuadrilla", null));

        // El ID y la fecha son cortos: así queda más espacio para los textos
        TableColumnModel columnas = getTabla().getColumnModel();
        columnas.getColumn(0).setMaxWidth(50);
        columnas.getColumn(2).setMaxWidth(90);

        // El formulario es alto, así que se acomoda en dos columnas
        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.gridx = 0;
        formulario.add(crearDatos(), c);
        c.gridx = 1;
        c.insets = new Insets(0, 10, 0, 0);
        formulario.add(crearColonias(), c);
        setFormulario(formulario);

        // Con Enter en el cuadro de búsqueda también se presiona Buscar
        txtBuscarColonia.addActionListener(e -> btnBuscar.doClick());
    }

    /** Primera columna: los datos de la actividad y la imagen de evidencia. */
    private JPanel crearDatos() {
        // La imagen se muestra en un recuadro de tamaño fijo (+2 por el borde)
        Dimension tamanoImagen = new Dimension(ANCHO_IMAGEN + 2, ALTO_IMAGEN + 2);
        lblImagen.setPreferredSize(tamanoImagen);
        lblImagen.setMinimumSize(tamanoImagen);
        lblImagen.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JPanel botonImagen = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        botonImagen.add(btnCargarImagen);
        JPanel evidencia = new JPanel(new BorderLayout(0, 5));
        evidencia.add(lblImagen, BorderLayout.WEST);
        evidencia.add(botonImagen, BorderLayout.SOUTH);

        // La barra vertical siempre se ve para que el ancho no cambie con textos largos
        JScrollPane scrollDetalle = new JScrollPane(txtDetalle,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        String[] etiquetas = {"Actividad:", "Detalle:", "Fecha:", "Lugar específico:", "Cuadrilla:", "Evidencia:"};
        JComponent[] campos = {txtNombre, scrollDetalle, spnFecha, txtLugar, cmbCuadrilla, evidencia};
        return crearFormulario(etiquetas, campos);
    }

    /** Segunda columna: búsqueda de colonias y lista de colonias atendidas. */
    private JPanel crearColonias() {
        lstResultados.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        lstColonias.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollResultados = new JScrollPane(lstResultados);
        scrollResultados.setPreferredSize(new Dimension(ANCHO_LISTA, ALTO_LISTA));
        JScrollPane scrollColonias = new JScrollPane(lstColonias);
        scrollColonias.setPreferredSize(new Dimension(ANCHO_LISTA, ALTO_LISTA));

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        panel.add(new JLabel("Buscar colonia (nombre o código postal):"), c);

        c.gridy = 1;
        c.gridwidth = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtBuscarColonia, c);
        c.gridx = 1;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        panel.add(btnBuscar, c);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        panel.add(scrollResultados, c);

        c.gridy = 3;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;
        panel.add(btnAgregar, c);

        c.gridy = 4;
        c.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Colonias atendidas:"), c);

        c.gridy = 5;
        c.fill = GridBagConstraints.BOTH;
        panel.add(scrollColonias, c);

        c.gridy = 6;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;
        panel.add(btnQuitar, c);
        return panel;
    }

    @Override
    public void limpiarFormulario() {
        txtNombre.setText("");
        txtDetalle.setText("");
        setFecha(LocalDate.now());
        txtLugar.setText("");
        cmbCuadrilla.setSelectedIndex(-1);
        txtBuscarColonia.setText("");
        modeloResultados.clear();
        modeloColonias.clear();
        mostrarImagen(null);
        txtNombre.requestFocusInWindow();
    }

    /**
     * Muestra la imagen de evidencia escalada para que quepa en el recuadro
     * sin deformarse. Si no hay imagen, muestra el texto "Sin imagen".
     */
    public void mostrarImagen(byte[] imagen) {
        ImageIcon icono = (imagen != null) ? new ImageIcon(imagen) : null;
        if (icono == null || icono.getIconWidth() <= 0) {
            lblImagen.setIcon(null);
            lblImagen.setText("Sin imagen");
            return;
        }
        // Las imágenes chicas no se agrandan
        double escala = Math.min(1.0, Math.min(
                (double) ANCHO_IMAGEN / icono.getIconWidth(),
                (double) ALTO_IMAGEN / icono.getIconHeight()));
        int ancho = Math.max(1, (int) Math.round(icono.getIconWidth() * escala));
        int alto = Math.max(1, (int) Math.round(icono.getIconHeight() * escala));
        lblImagen.setText(null);
        lblImagen.setIcon(new ImageIcon(icono.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH)));
    }

    /** Abre el diálogo para elegir la imagen; devuelve null si se canceló. */
    public File elegirImagen() {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Cargar imagen de evidencia");
        selector.setAcceptAllFileFilterUsed(false);
        selector.setFileFilter(new FileNameExtensionFilter("Imágenes (jpg, jpeg, png)", "jpg", "jpeg", "png"));
        if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            return selector.getSelectedFile();
        }
        return null;
    }

    public String getNombre() {
        return txtNombre.getText().trim();
    }

    public void setNombre(String nombre) {
        txtNombre.setText(nombre);
    }

    public String getDetalle() {
        return txtDetalle.getText().trim();
    }

    public void setDetalle(String detalle) {
        txtDetalle.setText(detalle);
        txtDetalle.setCaretPosition(0);
    }

    /** El JSpinner trabaja con Date; aquí se convierte a LocalDate. */
    public LocalDate getFecha() {
        Date fecha = (Date) spnFecha.getValue();
        return fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public void setFecha(LocalDate fecha) {
        spnFecha.setValue(Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    public String getLugar() {
        return txtLugar.getText().trim();
    }

    public void setLugar(String lugar) {
        txtLugar.setText(lugar);
    }

    /** Llena el combo de cuadrillas sin dejar ninguna elegida. */
    public void setCuadrillas(List<Cuadrilla> cuadrillas) {
        cmbCuadrilla.setModel(new DefaultComboBoxModel<>(cuadrillas.toArray(new Cuadrilla[0])));
        cmbCuadrilla.setSelectedIndex(-1);
    }

    /** Devuelve la cuadrilla elegida, o null si no se eligió ninguna. */
    public Cuadrilla getCuadrilla() {
        return (Cuadrilla) cmbCuadrilla.getSelectedItem();
    }

    public void setCuadrilla(Cuadrilla cuadrilla) {
        cmbCuadrilla.setSelectedItem(cuadrilla);
    }

    public String getTextoBusqueda() {
        return txtBuscarColonia.getText().trim();
    }

    public void setResultados(List<Colonia> colonias) {
        modeloResultados.clear();
        modeloResultados.addAll(colonias);
    }

    /** Colonias marcadas en la lista de resultados de la búsqueda. */
    public List<Colonia> getResultadosMarcados() {
        return lstResultados.getSelectedValuesList();
    }

    /** Devuelve una copia de la lista de colonias atendidas. */
    public List<Colonia> getColonias() {
        return Collections.list(modeloColonias.elements());
    }

    public void setColonias(List<Colonia> colonias) {
        modeloColonias.clear();
        modeloColonias.addAll(colonias);
    }

    /** Colonias marcadas en la lista de colonias atendidas. */
    public List<Colonia> getColoniasMarcadas() {
        return lstColonias.getSelectedValuesList();
    }

    public JButton getBtnBuscar() {
        return btnBuscar;
    }

    public JButton getBtnAgregar() {
        return btnAgregar;
    }

    public JButton getBtnQuitar() {
        return btnQuitar;
    }

    public JButton getBtnCargarImagen() {
        return btnCargarImagen;
    }
}
