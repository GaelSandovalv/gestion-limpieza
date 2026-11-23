package mx.limpieza.controlador;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mx.limpieza.modelo.Cuadrilla;
import mx.limpieza.modelo.dao.CuadrillaDAO;
import mx.limpieza.vista.CuadrillaVista;

/**
 * Controla el CRUD de cuadrillas: valida el formulario y usa CuadrillaDAO
 * para guardar los cambios en la base de datos.
 */
public class CuadrillaControlador {

    // Largo máximo del nombre (VARCHAR(100) en la tabla)
    private static final int LARGO_MAXIMO = 100;
    // Errores de MySQL: valor repetido en un campo único y registro que otra tabla usa
    private static final int ERROR_DUPLICADO = 1062;
    private static final int ERROR_REGISTRO_RELACIONADO = 1451;

    private final CuadrillaVista vista;
    private final CuadrillaDAO cuadrillaDAO = new CuadrillaDAO();
    // Id de la cuadrilla que se está editando; -1 cuando se captura una nueva
    private int idActual = -1;

    public CuadrillaControlador(CuadrillaVista vista) {
        this.vista = vista;
        vista.getBtnNuevo().addActionListener(e -> nuevo());
        vista.getBtnGuardar().addActionListener(e -> guardar());
        vista.getBtnEliminar().addActionListener(e -> eliminar());
        vista.getTabla().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarSeleccionado();
            }
        });
        cargarTabla();
    }

    private void cargarTabla() {
        try {
            List<Object[]> filas = new ArrayList<>();
            for (Cuadrilla cuadrilla : cuadrillaDAO.listar()) {
                // El jefe se marca en la pantalla de personal; puede no haberlo todavía
                String jefe = (cuadrilla.getJefe() != null) ? cuadrilla.getJefe().getNombreCompleto() : "Sin jefe";
                filas.add(new Object[] {cuadrilla.getId(), cuadrilla.getNombre(), jefe});
            }
            vista.setFilas(filas);
        } catch (SQLException e) {
            vista.mostrarError(MensajesBD.traducir(e));
        }
    }

    /** Carga en el formulario la cuadrilla que se seleccionó en la tabla. */
    private void mostrarSeleccionado() {
        int id = vista.getIdSeleccionado();
        if (id == -1) {
            return;
        }
        try {
            Cuadrilla cuadrilla = cuadrillaDAO.buscarPorId(id);
            if (cuadrilla != null) {
                vista.setNombre(cuadrilla.getNombre());
                idActual = id;
            }
        } catch (SQLException e) {
            vista.mostrarError(MensajesBD.traducir(e));
        }
    }

    private void nuevo() {
        vista.limpiarSeleccion();
        vista.limpiarFormulario();
        idActual = -1;
    }

    private void guardar() {
        String nombre = vista.getNombre();
        if (nombre.isEmpty()) {
            vista.mostrarError("Escriba el nombre de la cuadrilla.");
            return;
        }
        if (nombre.length() > LARGO_MAXIMO) {
            vista.mostrarError("El nombre no puede tener más de " + LARGO_MAXIMO + " caracteres.");
            return;
        }
        Cuadrilla cuadrilla = new Cuadrilla(idActual, nombre, null);
        try {
            String mensaje;
            if (idActual == -1) {
                cuadrillaDAO.insertar(cuadrilla);
                mensaje = "Cuadrilla registrada.";
            } else {
                cuadrillaDAO.actualizar(cuadrilla);
                mensaje = "Cuadrilla modificada.";
            }
            cargarTabla();
            nuevo();
            vista.mostrarMensaje(mensaje);
        } catch (SQLException e) {
            if (e.getErrorCode() == ERROR_DUPLICADO) {
                vista.mostrarError("Ya existe una cuadrilla con ese nombre.");
            } else {
                vista.mostrarError(MensajesBD.traducir(e));
            }
        }
    }

    private void eliminar() {
        int id = vista.getIdSeleccionado();
        if (id == -1) {
            vista.mostrarError("Seleccione en la tabla la cuadrilla que desea eliminar.");
            return;
        }
        if (!vista.confirmar("¿Desea eliminar la cuadrilla seleccionada?")) {
            return;
        }
        try {
            cuadrillaDAO.eliminar(id);
            cargarTabla();
            nuevo();
            vista.mostrarMensaje("Cuadrilla eliminada.");
        } catch (SQLException e) {
            if (e.getErrorCode() == ERROR_REGISTRO_RELACIONADO) {
                vista.mostrarError("No se puede eliminar la cuadrilla porque tiene personal o actividades registradas.");
            } else {
                vista.mostrarError(MensajesBD.traducir(e));
            }
        }
    }
}
