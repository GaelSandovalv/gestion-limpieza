package mx.limpieza.controlador;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mx.limpieza.modelo.Cuadrilla;
import mx.limpieza.modelo.Personal;
import mx.limpieza.modelo.dao.CuadrillaDAO;
import mx.limpieza.modelo.dao.PersonalDAO;
import mx.limpieza.vista.PersonalVista;

/**
 * Controla el CRUD del personal: llena el combo de cuadrillas, valida el
 * formulario y usa PersonalDAO para guardar los cambios en la base de datos.
 */
public class PersonalControlador {

    // Largo máximo de nombre y apellidos (VARCHAR(100) en la tabla)
    private static final int LARGO_MAXIMO = 100;

    private final PersonalVista vista;
    private final PersonalDAO personalDAO = new PersonalDAO();
    private final CuadrillaDAO cuadrillaDAO = new CuadrillaDAO();
    // Id de la persona que se está editando; -1 cuando se captura una nueva
    private int idActual = -1;

    public PersonalControlador(PersonalVista vista) {
        this.vista = vista;
        vista.getBtnNuevo().addActionListener(e -> nuevo());
        vista.getBtnGuardar().addActionListener(e -> guardar());
        vista.getBtnEliminar().addActionListener(e -> eliminar());
        vista.getTabla().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarSeleccionado();
            }
        });
        cargarDatos();
    }

    /** Llena el combo de cuadrillas y la tabla del personal. */
    private void cargarDatos() {
        try {
            vista.setCuadrillas(cuadrillaDAO.listar());
            List<Object[]> filas = new ArrayList<>();
            for (Personal personal : personalDAO.listar()) {
                filas.add(new Object[] {personal.getId(), personal.getNombre(), personal.getApellidos(),
                    personal.getCuadrilla().getNombre()});
            }
            vista.setFilas(filas);
        } catch (SQLException e) {
            vista.mostrarError(MensajesBD.traducir(e));
        }
    }

    /** Carga en el formulario a la persona que se seleccionó en la tabla. */
    private void mostrarSeleccionado() {
        int id = vista.getIdSeleccionado();
        if (id == -1) {
            return;
        }
        try {
            Personal personal = personalDAO.buscarPorId(id);
            if (personal != null) {
                vista.setNombre(personal.getNombre());
                vista.setApellidos(personal.getApellidos());
                vista.setCuadrilla(personal.getCuadrilla());
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
        Cuadrilla cuadrilla = vista.getCuadrilla();
        if (cuadrilla == null) {
            vista.mostrarError("Primero registre una cuadrilla.");
            return;
        }
        String error = validar(nombre, apellidos);
        if (error != null) {
            vista.mostrarError(error);
            return;
        }
        Personal personal = new Personal(idActual, nombre, apellidos, cuadrilla);
        try {
            String mensaje;
            if (idActual == -1) {
                personalDAO.insertar(personal);
                mensaje = "Personal registrado.";
            } else {
                personalDAO.actualizar(personal);
                mensaje = "Personal modificado.";
            }
            cargarDatos();
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
            vista.mostrarError("Seleccione en la tabla a la persona que desea eliminar.");
            return;
        }
        if (!vista.confirmar("¿Desea eliminar a la persona seleccionada?")) {
            return;
        }
        try {
            personalDAO.eliminar(id);
            cargarDatos();
            nuevo();
            vista.mostrarMensaje("Personal eliminado.");
        } catch (SQLException e) {
            vista.mostrarError(MensajesBD.traducir(e));
        }
    }
}
