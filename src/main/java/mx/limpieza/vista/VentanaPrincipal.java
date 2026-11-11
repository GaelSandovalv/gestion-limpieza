package mx.limpieza.vista;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Ventana principal: tiene el menú para navegar entre las opciones y un
 * área central donde se muestra la pantalla elegida.
 */
public class VentanaPrincipal extends JFrame {

    private final JMenu menuCatalogos = new JMenu("Catálogos");
    private final JMenu menuActividades = new JMenu("Actividades");
    private final JMenu menuConsultas = new JMenu("Consultas");
    private final JMenu menuSistema = new JMenu("Sistema");
    private final JMenuItem itemCerrarSistema = new JMenuItem("Cerrar sistema");
    private final JPanel contenido = new JPanel(new BorderLayout());
    private final JLabel lblAdministrador = new JLabel();

    public VentanaPrincipal() {
        super("Gestión de Actividades de Limpieza - Saltillo");
        // El controlador decide qué hacer al cerrar la ventana
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1200, 720);
        setLocationRelativeTo(null);

        JMenuBar barraMenu = new JMenuBar();
        menuSistema.add(itemCerrarSistema);
        barraMenu.add(menuCatalogos);
        barraMenu.add(menuActividades);
        barraMenu.add(menuConsultas);
        barraMenu.add(menuSistema);
        setJMenuBar(barraMenu);

        JLabel lblBienvenida = new JLabel("Seleccione una opción del menú", SwingConstants.CENTER);
        lblBienvenida.setFont(lblBienvenida.getFont().deriveFont(Font.BOLD, 20f));
        contenido.add(lblBienvenida, BorderLayout.CENTER);

        lblAdministrador.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        add(contenido, BorderLayout.CENTER);
        add(lblAdministrador, BorderLayout.SOUTH);
    }

    /** Agrega una opción al menú indicado y la devuelve para asignarle su acción. */
    public JMenuItem agregarOpcion(JMenu menu, String texto) {
        JMenuItem opcion = new JMenuItem(texto);
        menu.add(opcion);
        return opcion;
    }

    /** Cambia la pantalla que se muestra en el área central. */
    public void mostrarPanel(JPanel panel) {
        contenido.removeAll();
        contenido.add(panel, BorderLayout.CENTER);
        contenido.revalidate();
        contenido.repaint();
    }

    public void setNombreAdministrador(String nombre) {
        lblAdministrador.setText("Sesión iniciada: " + nombre);
    }

    public boolean confirmar(String mensaje) {
        return JOptionPane.showConfirmDialog(this, mensaje, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public JMenu getMenuCatalogos() {
        return menuCatalogos;
    }

    public JMenu getMenuActividades() {
        return menuActividades;
    }

    public JMenu getMenuConsultas() {
        return menuConsultas;
    }

    public JMenuItem getItemCerrarSistema() {
        return itemCerrarSistema;
    }
}
