package mx.limpieza.modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mx.limpieza.modelo.JefeCuadrilla;
import mx.limpieza.modelo.conexion.ConexionBD;

/**
 * Acceso a datos de la tabla jefe_cuadrilla.
 */
public class JefeCuadrillaDAO implements DAO<JefeCuadrilla> {

    private static final String COLUMNAS = "id, nombre, apellidos";

    @Override
    public void insertar(JefeCuadrilla jefe) throws SQLException {
        String sql = "INSERT INTO jefe_cuadrilla (nombre, apellidos) VALUES (?, ?)";
        try (PreparedStatement ps = conexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, jefe.getNombre());
            ps.setString(2, jefe.getApellidos());
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    jefe.setId(claves.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(JefeCuadrilla jefe) throws SQLException {
        String sql = "UPDATE jefe_cuadrilla SET nombre = ?, apellidos = ? WHERE id = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, jefe.getNombre());
            ps.setString(2, jefe.getApellidos());
            ps.setInt(3, jefe.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws SQLException {
        try (PreparedStatement ps = conexion().prepareStatement("DELETE FROM jefe_cuadrilla WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public JefeCuadrilla buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM jefe_cuadrilla WHERE id = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    @Override
    public List<JefeCuadrilla> listar() throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM jefe_cuadrilla ORDER BY nombre, apellidos";
        List<JefeCuadrilla> jefes = new ArrayList<>();
        try (PreparedStatement ps = conexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                jefes.add(mapear(rs));
            }
        }
        return jefes;
    }

    private JefeCuadrilla mapear(ResultSet rs) throws SQLException {
        return new JefeCuadrilla(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("apellidos"));
    }

    private Connection conexion() throws SQLException {
        return ConexionBD.getInstancia().getConexion();
    }
}
