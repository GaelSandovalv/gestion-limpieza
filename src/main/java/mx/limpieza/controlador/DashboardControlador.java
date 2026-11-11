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
 * Controla el dashboard por cuadrilla: carga el total de actividades, la tabla
 * resumen y la gráfica, y atiende la consulta (al seleccionar una cuadrilla
 * se listan sus actividades; al seleccionar una actividad se ve su detalle).
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
        vista.getTablaActividades().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalle();
            }
        });
        cargarResumen();
    }

    /** Carga el total general, la tabla resumen y la gráfica de barras. */
    private void cargarResumen() {
        try {
            vista.setTotalActividades(actividadDAO.contarTotal());
            List<Object[]> filas = new ArrayList<>();
            List<String> etiquetas = new ArrayList<>();
            List<Integer> valores = new ArrayList<>();
            for (ResumenCuadrilla resumen : actividadDAO.resumenPorCuadrilla()) {
                filas.add(new Object[] {
                    resumen.getCuadrillaId(),
                    resumen.getNombreCuadrilla(),
                    resumen.getNombreJefe(),
                    resumen.getNumPersonal(),
                    resumen.getNumActividades()
                });
                // La gráfica muestra las mismas cuadrillas, en el mismo orden que la tabla
                etiquetas.add(resumen.getNombreCuadrilla());
                valores.add(resumen.getNumActividades());
            }
            vista.setResumen(filas);
            vista.setDatosGrafica(etiquetas, valores);
        } catch (SQLException e) {
            vista.mostrarError(MensajesBD.traducir(e));
        }
    }

    /** Lista las actividades de la cuadrilla seleccionada en la tabla resumen. */
    private void mostrarActividades() {
        List<Object[]> filas = new ArrayList<>();
        int cuadrillaId = vista.getIdCuadrillaSeleccionada();
        if (cuadrillaId != -1) {
            try {
                for (Actividad actividad : actividadDAO.listarPorCuadrilla(cuadrillaId)) {
                    filas.add(new Object[] {
                        actividad.getId(),
                        actividad.getNombre(),
                        actividad.getFecha().format(FORMATO_FECHA),
                        actividad.getLugar()
                    });
                }
            } catch (SQLException e) {
                vista.mostrarError(MensajesBD.traducir(e));
            }
        }
        // Al cambiar la lista se pierde la actividad seleccionada y se limpia su detalle
        vista.setActividades(filas);
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
