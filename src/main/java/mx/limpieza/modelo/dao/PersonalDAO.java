package mx.limpieza.modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mx.limpieza.modelo.Cuadrilla;
import mx.limpieza.modelo.Personal;
import mx.limpieza.modelo.conexion.ConexionBD;

/**
 * Acceso a datos de la tabla personal, que guarda también a los jefes de
 * cuadrilla (columna es_jefe). Al consultar, trae también el id y el nombre
 * de su cuadrilla (JOIN con cuadrilla).
 */
public class PersonalDAO implements DAO<Personal> {

    private static final String CONSULTA = "SELECT p.id, p.nombre, p.apellidos, p.es_jefe,"
            + " c.id AS cuadrilla_id, c.nombre AS cuadrilla_nombre"
            + " FROM personal p"
            + " JOIN cuadrilla c ON c.id = p.cuadrilla_id";

    @Override
    public void insertar(Personal personal) throws SQLException {
        String sql = "INSERT INTO personal (nombre, apellidos, es_jefe, cuadrilla_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, personal.getNombre());
            ps.setString(2, personal.getApellidos());
            ps.setBoolean(3, personal.isEsJefe());
            ps.setInt(4, personal.getCuadrilla().getId());
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    personal.setId(claves.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Personal personal) throws SQLException {
        String sql = "UPDATE personal SET nombre = ?, apellidos = ?, es_jefe = ?, cuadrilla_id = ? WHERE id = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, personal.getNombre());
            ps.setString(2, personal.getApellidos());
            ps.setBoolean(3, personal.isEsJefe());
            ps.setInt(4, personal.getCuadrilla().getId());
            ps.setInt(5, personal.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws SQLException {
        try (PreparedStatement ps = conexion().prepareStatement("DELETE FROM personal WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Personal buscarPorId(int id) throws SQLException {
        String sql = CONSULTA + " WHERE p.id = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    @Override
    public List<Personal> listar() throws SQLException {
        String sql = CONSULTA + " ORDER BY p.nombre, p.apellidos";
        List<Personal> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    /** Devuelve el jefe de la cuadrilla indicada, o null si todavía no tiene. */
    public Personal buscarJefe(int cuadrillaId) throws SQLException {
        String sql = CONSULTA + " WHERE p.cuadrilla_id = ? AND p.es_jefe = TRUE";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, cuadrillaId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    private Personal mapear(ResultSet rs) throws SQLException {
        // Aquí solo hacen falta el id y el nombre de la cuadrilla (sin su jefe)
        Cuadrilla cuadrilla = new Cuadrilla(rs.getInt("cuadrilla_id"), rs.getString("cuadrilla_nombre"), null);
        return new Personal(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("apellidos"),
                rs.getBoolean("es_jefe"),
                cuadrilla);
    }

    private Connection conexion() throws SQLException {
        return ConexionBD.getInstancia().getConexion();
    }
}
