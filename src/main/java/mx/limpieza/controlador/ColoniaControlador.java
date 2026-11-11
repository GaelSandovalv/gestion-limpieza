package mx.limpieza.controlador;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mx.limpieza.modelo.Colonia;
import mx.limpieza.modelo.dao.ColoniaDAO;
import mx.limpieza.vista.ColoniaVista;

/**
 * Controla el CRUD de colonias: búsqueda, alta, consulta, modificación y baja.
 */
public class ColoniaControlador {

    // Longitudes máximas de las columnas de la tabla colonia
    private static final int MAXIMO_NOMBRE = 150;
    private static final int MAXIMO_TIPO = 50;

    // Códigos de error de MySQL que tienen un mensaje propio en esta pantalla
    private static final int ERROR_DUPLICADO = 1062;
    private static final int ERROR_REGISTRO_RELACIONADO = 1451;

    private final ColoniaVista vista;
    private final ColoniaDAO coloniaDAO = new ColoniaDAO();
    // Id de la colonia que se está editando; -1 si es una colonia nueva
    private int idActual = -1;

    public ColoniaControlador(ColoniaVista vista) {
        this.vista = vista;
        vista.getBtnNuevo().addActionListener(e -> nuevo());
        vista.getBtnGuardar().addActionListener(e -> guardar());
        vista.getBtnEliminar().addActionListener(e -> eliminar());
        vista.getBtnBuscar().addActionListener(e -> buscar());
        vista.getTabla().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionar();
            }
        });
        cargarTabla();
    }

    /** Muestra en la tabla las colonias que coinciden con la búsqueda (todas si está vacía). */
    private void cargarTabla() {
        try {
            List<Object[]> filas = new ArrayList<>();
            for (Colonia colonia : coloniaDAO.buscar(vista.getTextoBusqueda())) {
                filas.add(new Object[] {colonia.getId(), colonia.getCodigoPostal(),
                        colonia.getNombre(), colonia.getTipoAsentamiento()});
            }
            vista.setFilas(filas);
        } catch (SQLException e) {
            vista.mostrarError(MensajesBD.traducir(e));
        }
    }

    /**
     * Al buscar cambia la tabla y se pierde la fila seleccionada. Si se estaba
     * modificando una colonia, se limpia el formulario para no modificarla por error.
     */
    private void buscar() {
        if (idActual != -1) {
            nuevo();
        }
        cargarTabla();
    }

    /** Carga en el formulario la colonia seleccionada en la tabla. */
    private void seleccionar() {
        int id = vista.getIdSeleccionado();
        if (id == -1) {
            return;
        }
        try {
            Colonia colonia = coloniaDAO.buscarPorId(id);
            if (colonia != null) {
                vista.setCodigoPostal(colonia.getCodigoPostal());
                vista.setNombre(colonia.getNombre());
                vista.setTipoAsentamiento(colonia.getTipoAsentamiento());
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
        String codigoPostal = vista.getCodigoPostal();
        String nombre = vista.getNombre();
        String tipoAsentamiento = vista.getTipoAsentamiento();

        if (codigoPostal.isEmpty()) {
            vista.mostrarError("Escriba el código postal.");
            return;
        }
        if (!codigoPostal.matches("\\d{5}")) {
            vista.mostrarError("El código postal debe tener exactamente 5 dígitos.");
            return;
        }
        if (nombre.isEmpty()) {
            vista.mostrarError("Escriba el nombre de la colonia.");
            return;
        }
        if (nombre.length() > MAXIMO_NOMBRE) {
            vista.mostrarError("El nombre de la colonia no puede tener más de " + MAXIMO_NOMBRE + " caracteres.");
            return;
        }
        if (tipoAsentamiento.isEmpty()) {
            vista.mostrarError("Escriba el tipo de asentamiento.");
            return;
        }
        if (tipoAsentamiento.length() > MAXIMO_TIPO) {
            vista.mostrarError("El tipo de asentamiento no puede tener más de " + MAXIMO_TIPO + " caracteres.");
            return;
        }

        Colonia colonia = new Colonia(idActual, codigoPostal, nombre, tipoAsentamiento);
        try {
            String mensaje;
            if (idActual == -1) {
                coloniaDAO.insertar(colonia);
                mensaje = "La colonia se registró correctamente.";
            } else {
                coloniaDAO.actualizar(colonia);
                mensaje = "La colonia se modificó correctamente.";
            }
            cargarTabla();
            nuevo();
            vista.mostrarMensaje(mensaje);
        } catch (SQLException e) {
            if (e.getErrorCode() == ERROR_DUPLICADO) {
                vista.mostrarError("Ya existe esa colonia.");
            } else {
                vista.mostrarError(MensajesBD.traducir(e));
            }
        }
    }

    private void eliminar() {
        int id = vista.getIdSeleccionado();
        if (id == -1) {
            vista.mostrarError("Seleccione en la tabla la colonia que desea eliminar.");
            return;
        }
        if (!vista.confirmar("¿Desea eliminar la colonia seleccionada?")) {
            return;
        }
        try {
            coloniaDAO.eliminar(id);
            cargarTabla();
            nuevo();
            vista.mostrarMensaje("La colonia se eliminó correctamente.");
        } catch (SQLException e) {
            if (e.getErrorCode() == ERROR_REGISTRO_RELACIONADO) {
                vista.mostrarError("No se puede eliminar la colonia porque está registrada en una o más actividades.");
            } else {
                vista.mostrarError(MensajesBD.traducir(e));
            }
        }
    }
}
