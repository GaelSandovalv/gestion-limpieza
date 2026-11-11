package mx.limpieza.modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import mx.limpieza.modelo.Actividad;
import mx.limpieza.modelo.Colonia;
import mx.limpieza.modelo.Cuadrilla;
import mx.limpieza.modelo.JefeCuadrilla;
import mx.limpieza.modelo.conexion.ConexionBD;

/**
 * Acceso a datos de la tabla actividad y de sus colonias atendidas
 * (tabla actividad_colonia). Guardar una actividad modifica las dos tablas,
 * por eso insertar y actualizar usan una transacción: se guarda todo o nada.
 */
public class ActividadDAO implements DAO<Actividad> {

    // Consulta ligera para llenar tablas: no trae la imagen ni las colonias
    private static final String CONSULTA_LIGERA = "SELECT a.id, a.nombre, a.detalle, a.fecha, a.lugar,"
            + " c.id AS cuadrilla_id, c.nombre AS cuadrilla_nombre"
            + " FROM actividad a"
            + " JOIN cuadrilla c ON c.id = a.cuadrilla_id";

    // Primero las más recientes
    private static final String ORDEN = " ORDER BY a.fecha DESC, a.id DESC";

    @Override
    public void insertar(Actividad actividad) throws SQLException {
        Connection con = conexion();
        // Inicia la transacción: nada queda guardado hasta el commit
        con.setAutoCommit(false);
        try {
            String sql = "INSERT INTO actividad (nombre, detalle, fecha, lugar, cuadrilla_id, evidencia)"
                    + " VALUES (?, ?, ?, ?, ?, ?)";
            int id = 0;
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                asignarDatos(ps, actividad);
                ps.executeUpdate();
                try (ResultSet claves = ps.getGeneratedKeys()) {
                    if (claves.next()) {
                        id = claves.getInt(1);
                    }
                }
            }
            insertarColonias(con, id, actividad.getColonias());
            con.commit();
            // El id se asigna solo si la transacción se confirmó
            actividad.setId(id);
        } catch (SQLException | RuntimeException e) {
            // Si algo falló, se deshace todo lo que se hizo en la transacción
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    @Override
    public void actualizar(Actividad actividad) throws SQLException {
        Connection con = conexion();
        con.setAutoCommit(false);
        try {
            String sql = "UPDATE actividad SET nombre = ?, detalle = ?, fecha = ?, lugar = ?,"
                    + " cuadrilla_id = ?, evidencia = ? WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                asignarDatos(ps, actividad);
                ps.setInt(7, actividad.getId());
                ps.executeUpdate();
            }
            // Se borran las colonias anteriores y se guardan las de ahora
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM actividad_colonia WHERE actividad_id = ?")) {
                ps.setInt(1, actividad.getId());
                ps.executeUpdate();
            }
            insertarColonias(con, actividad.getId(), actividad.getColonias());
            con.commit();
        } catch (SQLException | RuntimeException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    /** Elimina la actividad; sus colonias atendidas se borran en cascada. */
    @Override
    public void eliminar(int id) throws SQLException {
        try (PreparedStatement ps = conexion().prepareStatement("DELETE FROM actividad WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /** Trae la actividad completa: su cuadrilla con el jefe, sus colonias y la evidencia. */
    @Override
    public Actividad buscarPorId(int id) throws SQLException {
        String sql = "SELECT a.id, a.nombre, a.detalle, a.fecha, a.lugar, a.evidencia,"
                + " c.id AS cuadrilla_id, c.nombre AS cuadrilla_nombre,"
                + " j.id AS jefe_id, j.nombre AS jefe_nombre, j.apellidos AS jefe_apellidos"
                + " FROM actividad a"
                + " JOIN cuadrilla c ON c.id = a.cuadrilla_id"
                + " JOIN jefe_cuadrilla j ON j.id = c.jefe_id"
                + " WHERE a.id = ?";
        Actividad actividad = null;
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    actividad = mapear(rs);
                    actividad.getCuadrilla().setJefe(new JefeCuadrilla(
                            rs.getInt("jefe_id"),
                            rs.getString("jefe_nombre"),
                            rs.getString("jefe_apellidos")));
                    actividad.setEvidencia(rs.getBytes("evidencia"));
                }
            }
        }
        if (actividad != null) {
            actividad.setColonias(listarColonias(id));
        }
        return actividad;
    }

    /** Devuelve todas las actividades sin la evidencia ni las colonias. */
    @Override
    public List<Actividad> listar() throws SQLException {
        List<Actividad> actividades = new ArrayList<>();
        try (PreparedStatement ps = conexion().prepareStatement(CONSULTA_LIGERA + ORDEN);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                actividades.add(mapear(rs));
            }
        }
        return actividades;
    }

    /** Pasa los datos de la actividad a los parámetros 1 a 6 de la sentencia. */
    private void asignarDatos(PreparedStatement ps, Actividad actividad) throws SQLException {
        ps.setString(1, actividad.getNombre());
        ps.setString(2, actividad.getDetalle());
        ps.setObject(3, actividad.getFecha());
        ps.setString(4, actividad.getLugar());
        ps.setInt(5, actividad.getCuadrilla().getId());
        if (actividad.getEvidencia() == null) {
            ps.setNull(6, Types.LONGVARBINARY);
        } else {
            ps.setBytes(6, actividad.getEvidencia());
        }
    }

    /** Guarda las colonias atendidas usando la misma conexión de la transacción. */
    private void insertarColonias(Connection con, int actividadId, List<Colonia> colonias) throws SQLException {
        String sql = "INSERT INTO actividad_colonia (actividad_id, colonia_id) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Colonia colonia : colonias) {
                ps.setInt(1, actividadId);
                ps.setInt(2, colonia.getId());
                ps.executeUpdate();
            }
        }
    }

    private List<Colonia> listarColonias(int actividadId) throws SQLException {
        String sql = "SELECT co.id, co.codigo_postal, co.nombre, co.tipo_asentamiento"
                + " FROM actividad_colonia ac"
                + " JOIN colonia co ON co.id = ac.colonia_id"
                + " WHERE ac.actividad_id = ?"
                + " ORDER BY co.nombre, co.codigo_postal";
        List<Colonia> colonias = new ArrayList<>();
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, actividadId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    colonias.add(new Colonia(
                            rs.getInt("id"),
                            rs.getString("codigo_postal"),
                            rs.getString("nombre"),
                            rs.getString("tipo_asentamiento")));
                }
            }
        }
        return colonias;
    }

    private Actividad mapear(ResultSet rs) throws SQLException {
        // De la cuadrilla solo se leen el id y el nombre
        Cuadrilla cuadrilla = new Cuadrilla(rs.getInt("cuadrilla_id"), rs.getString("cuadrilla_nombre"), null);
        return new Actividad(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("detalle"),
                rs.getObject("fecha", LocalDate.class),
                rs.getString("lugar"),
                cuadrilla);
    }

    private Connection conexion() throws SQLException {
        return ConexionBD.getInstancia().getConexion();
    }
}
