package mx.limpieza.controlador;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import mx.limpieza.modelo.Actividad;
import mx.limpieza.modelo.Colonia;
import mx.limpieza.modelo.Cuadrilla;
import mx.limpieza.modelo.dao.ActividadDAO;
import mx.limpieza.modelo.dao.ColoniaDAO;
import mx.limpieza.modelo.dao.CuadrillaDAO;
import mx.limpieza.modelo.util.ImagenUtil;
import mx.limpieza.vista.ActividadVista;

/**
 * Controla el CRUD de actividades de limpieza: valida los datos, maneja las
 * colonias atendidas y la imagen de evidencia, y guarda todo con ActividadDAO.
 */
public class ActividadControlador {

    // Longitudes máximas de las columnas de la tabla actividad
    private static final int MAX_NOMBRE = 100;
    private static final int MAX_LUGAR = 200;
    // Una columna TEXT de MySQL guarda como máximo 65,535 bytes
    private static final int MAX_BYTES_DETALLE = 65535;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ActividadVista vista;
    private final ActividadDAO actividadDAO = new ActividadDAO();
    private final CuadrillaDAO cuadrillaDAO = new CuadrillaDAO();
    private final ColoniaDAO coloniaDAO = new ColoniaDAO();
    private List<Cuadrilla> cuadrillas = new ArrayList<>();
    private int idActual = -1;
    // Imagen de la actividad que está en el formulario (null si no tiene)
    private byte[] evidenciaActual;

    public ActividadControlador(ActividadVista vista) {
        this.vista = vista;
        vista.getBtnNuevo().addActionListener(e -> limpiar());
        vista.getBtnGuardar().addActionListener(e -> guardar());
        vista.getBtnEliminar().addActionListener(e -> eliminar());
        vista.getBtnBuscar().addActionListener(e -> buscarColonias());
        vista.getBtnAgregar().addActionListener(e -> agregarColonias());
        vista.getBtnQuitar().addActionListener(e -> quitarColonias());
        vista.getBtnCargarImagen().addActionListener(e -> cargarImagen());
        vista.getTabla().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionar();
            }
        });
        cargarCuadrillas();
        cargarTabla();
    }

    private void cargarCuadrillas() {
        try {
            cuadrillas = cuadrillaDAO.listar();
            vista.setCuadrillas(cuadrillas);
        } catch (SQLException e) {
            vista.mostrarError(MensajesBD.traducir(e));
        }
    }

    private void cargarTabla() {
        try {
            List<Object[]> filas = new ArrayList<>();
            for (Actividad actividad : actividadDAO.listar()) {
                filas.add(new Object[]{
                    actividad.getId(),
                    actividad.getNombre(),
                    actividad.getFecha().format(FORMATO_FECHA),
                    actividad.getLugar(),
                    actividad.getCuadrilla().getNombre()
                });
            }
            vista.setFilas(filas);
        } catch (SQLException e) {
            vista.mostrarError(MensajesBD.traducir(e));
        }
    }

    /** Carga en el formulario la actividad completa de la fila seleccionada. */
    private void seleccionar() {
        int id = vista.getIdSeleccionado();
        if (id == -1) {
            return;
        }
        try {
            Actividad actividad = actividadDAO.buscarPorId(id);
            if (actividad == null) {
                vista.mostrarError("La actividad seleccionada ya no existe.");
                return;
            }
            vista.setNombre(actividad.getNombre());
            vista.setDetalle(actividad.getDetalle());
            vista.setFecha(actividad.getFecha());
            vista.setLugar(actividad.getLugar());
            vista.setCuadrilla(buscarCuadrilla(actividad.getCuadrilla().getId()));
            vista.setColonias(actividad.getColonias());
            evidenciaActual = actividad.getEvidencia();
            vista.mostrarImagen(evidenciaActual);
            idActual = id;
        } catch (SQLException e) {
            vista.mostrarError(MensajesBD.traducir(e));
        }
    }

    /** Deja el formulario listo para capturar una actividad nueva. */
    private void limpiar() {
        vista.limpiarFormulario();
        vista.limpiarSeleccion();
        idActual = -1;
        evidenciaActual = null;
    }

    private void guardar() {
        Actividad actividad = new Actividad(0, vista.getNombre(), vista.getDetalle(),
                vista.getFecha(), vista.getLugar(), vista.getCuadrilla());
        actividad.setColonias(vista.getColonias());
        // Si no se cargó una imagen nueva, se conserva la que ya tenía
        actividad.setEvidencia(evidenciaActual);

        String error = validar(actividad);
        if (error != null) {
            vista.mostrarError(error);
            return;
        }
        try {
            String mensaje;
            if (idActual == -1) {
                actividadDAO.insertar(actividad);
                mensaje = "Actividad registrada.";
            } else {
                actividad.setId(idActual);
                actividadDAO.actualizar(actividad);
                mensaje = "Actividad actualizada.";
            }
            cargarTabla();
            limpiar();
            vista.mostrarMensaje(mensaje);
        } catch (SQLException e) {
            vista.mostrarError(MensajesBD.traducir(e));
        }
    }

    /** Devuelve el mensaje del primer dato inválido, o null si todo está bien. */
    private String validar(Actividad actividad) {
        if (actividad.getNombre().isEmpty()) {
            return "Escriba el nombre de la actividad.";
        }
        if (actividad.getNombre().length() > MAX_NOMBRE) {
            return "El nombre de la actividad no puede tener más de " + MAX_NOMBRE + " caracteres.";
        }
        if (actividad.getDetalle().isEmpty()) {
            return "Escriba el detalle de la actividad.";
        }
        // TEXT mide bytes, no letras: en UTF-8 una letra con acento ocupa 2 bytes
        if (actividad.getDetalle().getBytes(StandardCharsets.UTF_8).length > MAX_BYTES_DETALLE) {
            return "El detalle de la actividad es demasiado largo.";
        }
        if (actividad.getFecha().isAfter(LocalDate.now())) {
            return "La fecha no puede ser posterior a hoy.";
        }
        // Si se escribe el año corto (26 en lugar de 2026), el selector lo toma como el año 26
        if (actividad.getFecha().getYear() < 2000) {
            return "Escriba la fecha con el año completo (dd/mm/aaaa).";
        }
        if (actividad.getLugar().isEmpty()) {
            return "Escriba el lugar específico.";
        }
        if (actividad.getLugar().length() > MAX_LUGAR) {
            return "El lugar específico no puede tener más de " + MAX_LUGAR + " caracteres.";
        }
        if (cuadrillas.isEmpty()) {
            return "Primero registre una cuadrilla.";
        }
        if (actividad.getCuadrilla() == null) {
            return "Seleccione la cuadrilla.";
        }
        if (actividad.getColonias().isEmpty()) {
            return "Agregue al menos una colonia atendida.";
        }
        return null;
    }

    private void eliminar() {
        int id = vista.getIdSeleccionado();
        if (id == -1) {
            vista.mostrarError("Seleccione la actividad que desea eliminar.");
            return;
        }
        if (!vista.confirmar("¿Desea eliminar la actividad seleccionada?")) {
            return;
        }
        try {
            actividadDAO.eliminar(id);
            cargarTabla();
            limpiar();
            vista.mostrarMensaje("Actividad eliminada.");
        } catch (SQLException e) {
            vista.mostrarError(MensajesBD.traducir(e));
        }
    }

    private void buscarColonias() {
        try {
            vista.setResultados(coloniaDAO.buscar(vista.getTextoBusqueda()));
        } catch (SQLException e) {
            vista.mostrarError(MensajesBD.traducir(e));
        }
    }

    /** Pasa las colonias marcadas en los resultados a la lista de colonias atendidas. */
    private void agregarColonias() {
        List<Colonia> marcadas = vista.getResultadosMarcados();
        if (marcadas.isEmpty()) {
            vista.mostrarError("Seleccione en los resultados las colonias que desea agregar.");
            return;
        }
        List<Colonia> colonias = vista.getColonias();
        for (Colonia colonia : marcadas) {
            // Una colonia que ya está en la lista no se agrega otra vez
            if (!contieneColonia(colonias, colonia.getId())) {
                colonias.add(colonia);
            }
        }
        vista.setColonias(colonias);
    }

    private void quitarColonias() {
        List<Colonia> marcadas = vista.getColoniasMarcadas();
        if (marcadas.isEmpty()) {
            vista.mostrarError("Seleccione las colonias que desea quitar.");
            return;
        }
        List<Colonia> colonias = vista.getColonias();
        colonias.removeAll(marcadas);
        vista.setColonias(colonias);
    }

    /** Reduce la imagen elegida con ImagenUtil y muestra la vista previa. */
    private void cargarImagen() {
        File archivo = vista.elegirImagen();
        if (archivo == null) {
            return;
        }
        try {
            evidenciaActual = ImagenUtil.prepararEvidencia(archivo);
            vista.mostrarImagen(evidenciaActual);
        } catch (IOException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    /** Las colonias se comparan por id porque pueden venir de consultas distintas. */
    private boolean contieneColonia(List<Colonia> colonias, int id) {
        for (Colonia colonia : colonias) {
            if (colonia.getId() == id) {
                return true;
            }
        }
        return false;
    }

    /** Busca en el combo la cuadrilla con ese id (la de la actividad es otro objeto). */
    private Cuadrilla buscarCuadrilla(int id) {
        for (Cuadrilla cuadrilla : cuadrillas) {
            if (cuadrilla.getId() == id) {
                return cuadrilla;
            }
        }
        return null;
    }
}
