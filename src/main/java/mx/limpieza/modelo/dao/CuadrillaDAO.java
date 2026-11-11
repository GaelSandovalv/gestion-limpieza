package mx.limpieza.modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mx.limpieza.modelo.Cuadrilla;
import mx.limpieza.modelo.JefeCuadrilla;
import mx.limpieza.modelo.conexion.ConexionBD;

/**
 * Acceso a datos de la tabla cuadrilla. Al consultar, trae también
 * los datos de su jefe (JOIN con jefe_cuadrilla).
 */
public class CuadrillaDAO implements DAO<Cuadrilla> {

    private static final String CONSULTA = "SELECT c.id, c.nombre,"
            + " j.id AS jefe_id, j.nombre AS jefe_nombre, j.apellidos AS jefe_apellidos"
            + " FROM cuadrilla c"
            + " JOIN jefe_cuadrilla j ON j.id = c.jefe_id";

    @Override
    public void insertar(Cuadrilla cuadrilla) throws SQLException {
        String sql = "INSERT INTO cuadrilla (nombre, jefe_id) VALUES (?, ?)";
        try (PreparedStatement ps = conexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cuadrilla.getNombre());
            ps.setInt(2, cuadrilla.getJefe().getId());
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    cuadrilla.setId(claves.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Cuadrilla cuadrilla) throws SQLException {
        String sql = "UPDATE cuadrilla SET nombre = ?, jefe_id = ? WHERE id = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, cuadrilla.getNombre());
            ps.setInt(2, cuadrilla.getJefe().getId());
            ps.setInt(3, cuadrilla.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws SQLException {
        try (PreparedStatement ps = conexion().prepareStatement("DELETE FROM cuadrilla WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Cuadrilla buscarPorId(int id) throws SQLException {
        String sql = CONSULTA + " WHERE c.id = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    @Override
    public List<Cuadrilla> listar() throws SQLException {
        String sql = CONSULTA + " ORDER BY c.nombre";
        List<Cuadrilla> cuadrillas = new ArrayList<>();
        try (PreparedStatement ps = conexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cuadrillas.add(mapear(rs));
            }
        }
        return cuadrillas;
    }

    private Cuadrilla mapear(ResultSet rs) throws SQLException {
        JefeCuadrilla jefe = new JefeCuadrilla(
                rs.getInt("jefe_id"),
                rs.getString("jefe_nombre"),
                rs.getString("jefe_apellidos"));
        return new Cuadrilla(rs.getInt("id"), rs.getString("nombre"), jefe);
    }

    private Connection conexion() throws SQLException {
        return ConexionBD.getInstancia().getConexion();
    }
}
