package mx.limpieza.controlador;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import mx.limpieza.modelo.Actividad;
import mx.limpieza.modelo.ResumenCuadrilla;
import mx.limpieza.modelo.dao.ActividadDAO;
import mx.limpieza.vista.DashboardVista;

/**
 * Controla el dashboard por cuadrilla: carga los totales de actividades
 * terminadas y pendientes, la tabla resumen y la gráfica, y atiende la consulta
 * (al seleccionar una cuadrilla se listan sus actividades pendientes y
 * terminadas; al seleccionar una actividad se ve su detalle).
 */
public class DashboardControlador {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DashboardVista vista;
    private final ActividadDAO actividadDAO = new ActividadDAO();

    public DashboardControlador(DashboardVista vista) {
        this.vista = vista;
        vista.getTablaResumen().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarActividades();
            }
        });
        vista.getTablaPendientes().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalle();
            }
        });
        vista.getTablaTerminadas().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalle();
            }
        });
        // Al cambiar de pestaña se muestra el detalle de lo seleccionado en la otra pestaña
        vista.getPestanas().addChangeListener(e -> mostrarDetalle());
        cargarResumen();
    }

    /** Carga los totales, la tabla resumen y la gráfica de barras. */
    private void cargarResumen() {
        try {
            vista.setTotales(actividadDAO.contarPorEstado(true), actividadDAO.contarPorEstado(false));
            List<Object[]> filas = new ArrayList<>();
            List<String> etiquetas = new ArrayList<>();
            List<Integer> terminadas = new ArrayList<>();
            List<Integer> pendientes = new ArrayList<>();
            for (ResumenCuadrilla resumen : actividadDAO.resumenPorCuadrilla()) {
                filas.add(new Object[] {
                    resumen.getCuadrillaId(),
                    resumen.getNombreCuadrilla(),
                    (resumen.getNombreJefe() != null) ? resumen.getNombreJefe() : "Sin jefe",
                    resumen.getNumPersonal(),
                    resumen.getNumTerminadas(),
                    resumen.getNumPendientes()
                });
                // La gráfica muestra las mismas cuadrillas, en el mismo orden que la tabla
                etiquetas.add(resumen.getNombreCuadrilla());
                terminadas.add(resumen.getNumTerminadas());
                pendientes.add(resumen.getNumPendientes());
            }
            vista.setResumen(filas);
            vista.setDatosGrafica(etiquetas, terminadas, pendientes);
        } catch (SQLException e) {
            vista.mostrarError(MensajesBD.traducir(e));
        }
    }

    /** Lista las actividades de la cuadrilla seleccionada, separadas en pendientes y terminadas. */
    private void mostrarActividades() {
        List<Object[]> pendientes = new ArrayList<>();
        List<Object[]> terminadas = new ArrayList<>();
        int cuadrillaId = vista.getIdCuadrillaSeleccionada();
        if (cuadrillaId != -1) {
            try {
                for (Actividad actividad : actividadDAO.listarPorCuadrilla(cuadrillaId)) {
                    Object[] fila = {
                        actividad.getId(),
                        actividad.getNombre(),
                        actividad.getFecha().format(FORMATO_FECHA),
                        actividad.getLugar()
                    };
                    if (actividad.isTerminada()) {
                        terminadas.add(fila);
                    } else {
                        pendientes.add(fila);
                    }
                }
            } catch (SQLException e) {
                vista.mostrarError(MensajesBD.traducir(e));
            }
        }
        // Al cambiar las listas se pierde la actividad seleccionada y se limpia su detalle
        vista.setActividades(pendientes, terminadas);
        vista.limpiarDetalle();
    }

    /** Muestra el detalle, las colonias y la evidencia de la actividad seleccionada. */
    private void mostrarDetalle() {
        vista.limpiarDetalle();
        int actividadId = vista.getIdActividadSeleccionada();
        if (actividadId == -1) {
            return;
        }
        try {
            // buscarPorId trae la actividad completa: con sus colonias y su imagen
            Actividad actividad = actividadDAO.buscarPorId(actividadId);
            if (actividad != null) {
                vista.setDetalle(actividad.getDetalle());
                vista.setColonias(actividad.getColonias());
                vista.setImagen(actividad.getEvidencia());
            }
        } catch (SQLException e) {
            vista.mostrarError(MensajesBD.traducir(e));
        }
    }
}
