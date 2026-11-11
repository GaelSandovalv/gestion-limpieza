package mx.limpieza.modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mx.limpieza.modelo.Administrador;
import mx.limpieza.modelo.conexion.ConexionBD;

/**
 * Acceso a datos de la tabla administrador.
 */
public class AdministradorDAO implements DAO<Administrador> {

    private static final String COLUMNAS = "id, usuario, contrasena_hash, nombre";

    @Override
    public void insertar(Administrador administrador) throws SQLException {
        String sql = "INSERT INTO administrador (usuario, contrasena_hash, nombre) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, administrador.getUsuario());
            ps.setString(2, administrador.getContrasenaHash());
            ps.setString(3, administrador.getNombre());
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    administrador.setId(claves.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Administrador administrador) throws SQLException {
        String sql = "UPDATE administrador SET usuario = ?, contrasena_hash = ?, nombre = ? WHERE id = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, administrador.getUsuario());
            ps.setString(2, administrador.getContrasenaHash());
            ps.setString(3, administrador.getNombre());
            ps.setInt(4, administrador.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws SQLException {
        try (PreparedStatement ps = conexion().prepareStatement("DELETE FROM administrador WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Administrador buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM administrador WHERE id = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    @Override
    public List<Administrador> listar() throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM administrador ORDER BY nombre";
        List<Administrador> administradores = new ArrayList<>();
        try (PreparedStatement ps = conexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                administradores.add(mapear(rs));
            }
        }
        return administradores;
    }

    /** Busca al administrador por su nombre de usuario (se usa en el login). */
    public Administrador buscarPorUsuario(String usuario) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM administrador WHERE usuario = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    private Administrador mapear(ResultSet rs) throws SQLException {
        return new Administrador(
                rs.getInt("id"),
                rs.getString("usuario"),
                rs.getString("contrasena_hash"),
                rs.getString("nombre"));
    }

    private Connection conexion() throws SQLException {
        return ConexionBD.getInstancia().getConexion();
    }
}
