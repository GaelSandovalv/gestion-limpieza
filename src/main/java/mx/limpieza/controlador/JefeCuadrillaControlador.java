package mx.limpieza.controlador;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mx.limpieza.modelo.JefeCuadrilla;
import mx.limpieza.modelo.dao.JefeCuadrillaDAO;
import mx.limpieza.vista.JefeCuadrillaVista;

/**
 * Controla el CRUD de jefes de cuadrilla: valida el formulario y usa
 * JefeCuadrillaDAO para guardar los cambios en la base de datos.
 */
public class JefeCuadrillaControlador {

    // Largo máximo de nombre y apellidos (VARCHAR(100) en la tabla)
    private static final int LARGO_MAXIMO = 100;
    // Error de MySQL al borrar un registro que otra tabla usa
    private static final int ERROR_REGISTRO_RELACIONADO = 1451;

    private final JefeCuadrillaVista vista;
    private final JefeCuadrillaDAO jefeDAO = new JefeCuadrillaDAO();
    // Id del jefe que se está editando; -1 cuando se captura uno nuevo
    private int idActual = -1;

    public JefeCuadrillaControlador(JefeCuadrillaVista vista) {
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
            for (JefeCuadrilla jefe : jefeDAO.listar()) {
                filas.add(new Object[] {jefe.getId(), jefe.getNombre(), jefe.getApellidos()});
            }
            vista.setFilas(filas);
        } catch (SQLException e) {
            vista.mostrarError(MensajesBD.traducir(e));
        }
    }

    /** Carga en el formulario el jefe que se seleccionó en la tabla. */
    private void mostrarSeleccionado() {
        int id = vista.getIdSeleccionado();
        if (id == -1) {
            return;
        }
        try {
            JefeCuadrilla jefe = jefeDAO.buscarPorId(id);
            if (jefe != null) {
                vista.setNombre(jefe.getNombre());
                vista.setApellidos(jefe.getApellidos());
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
        String apellidos = vista.getApellidos();
        String error = validar(nombre, apellidos);
        if (error != null) {
            vista.mostrarError(error);
            return;
        }
        JefeCuadrilla jefe = new JefeCuadrilla(idActual, nombre, apellidos);
        try {
            String mensaje;
            if (idActual == -1) {
                jefeDAO.insertar(jefe);
                mensaje = "Jefe de cuadrilla registrado.";
            } else {
                jefeDAO.actualizar(jefe);
                mensaje = "Jefe de cuadrilla modificado.";
            }
            cargarTabla();
            nuevo();
            vista.mostrarMensaje(mensaje);
        } catch (SQLException e) {
            vista.mostrarError(MensajesBD.traducir(e));
        }
    }

    /** Devuelve el error de los datos capturados, o null si están bien. */
    private String validar(String nombre, String apellidos) {
        if (nombre.isEmpty()) {
            return "Escriba el nombre.";
        }
        if (nombre.length() > LARGO_MAXIMO) {
            return "El nombre no puede tener más de " + LARGO_MAXIMO + " caracteres.";
        }
        if (apellidos.isEmpty()) {
            return "Escriba los apellidos.";
        }
        if (apellidos.length() > LARGO_MAXIMO) {
            return "Los apellidos no pueden tener más de " + LARGO_MAXIMO + " caracteres.";
        }
        return null;
    }

    private void eliminar() {
        int id = vista.getIdSeleccionado();
        if (id == -1) {
            vista.mostrarError("Seleccione en la tabla el jefe de cuadrilla que desea eliminar.");
            return;
        }
        if (!vista.confirmar("¿Desea eliminar al jefe de cuadrilla seleccionado?")) {
            return;
        }
        try {
            jefeDAO.eliminar(id);
            cargarTabla();
            nuevo();
            vista.mostrarMensaje("Jefe de cuadrilla eliminado.");
        } catch (SQLException e) {
            if (e.getErrorCode() == ERROR_REGISTRO_RELACIONADO) {
                vista.mostrarError("No se puede eliminar el jefe porque está a cargo de una cuadrilla.");
            } else {
                vista.mostrarError(MensajesBD.traducir(e));
            }
        }
    }
}
