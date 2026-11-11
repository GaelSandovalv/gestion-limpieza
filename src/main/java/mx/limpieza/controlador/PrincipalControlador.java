package mx.limpieza.controlador;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import mx.limpieza.modelo.conexion.ConexionBD;
import mx.limpieza.modelo.sesion.Sesion;
import mx.limpieza.vista.ColoniaVista;
import mx.limpieza.vista.CuadrillaVista;
import mx.limpieza.vista.JefeCuadrillaVista;
import mx.limpieza.vista.VentanaPrincipal;

/**
 * Controla la ventana principal: el menú de navegación y el cierre del sistema.
 */
public class PrincipalControlador {

    private final VentanaPrincipal vista;

    public PrincipalControlador(VentanaPrincipal vista) {
        this.vista = vista;
        vista.getItemCerrarSistema().addActionListener(e -> cerrarSistema());
        vista.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarSistema();
            }
        });
        vista.agregarOpcion(vista.getMenuCatalogos(), "Colonias").addActionListener(e -> abrirColonias());
        vista.agregarOpcion(vista.getMenuCatalogos(), "Jefes de cuadrilla").addActionListener(e -> abrirJefesCuadrilla());
        vista.agregarOpcion(vista.getMenuCatalogos(), "Cuadrillas").addActionListener(e -> abrirCuadrillas());
    }

    public void iniciar() {
        if (Sesion.getInstancia().estaActiva()) {
            vista.setNombreAdministrador(Sesion.getInstancia().getAdministrador().getNombre());
        }
        vista.setVisible(true);
    }

    /**
     * Cierra el sistema de forma segura: destruye la sesión, cierra la
     * conexión a la base de datos y termina la aplicación.
     */
    private void cerrarSistema() {
        if (!vista.confirmar("¿Desea cerrar el sistema?")) {
            return;
        }
        Sesion.destruir();
        ConexionBD.cerrar();
        vista.dispose();
        System.exit(0);
    }

    private void abrirColonias() {
        ColoniaVista panel = new ColoniaVista();
        new ColoniaControlador(panel);
        vista.mostrarPanel(panel);
    }

    private void abrirJefesCuadrilla() {
        JefeCuadrillaVista panel = new JefeCuadrillaVista();
        new JefeCuadrillaControlador(panel);
        vista.mostrarPanel(panel);
    }

    private void abrirCuadrillas() {
        CuadrillaVista panel = new CuadrillaVista();
        new CuadrillaControlador(panel);
        vista.mostrarPanel(panel);
    }
}
