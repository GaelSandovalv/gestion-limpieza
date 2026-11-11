package mx.limpieza.modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mx.limpieza.modelo.Colonia;
import mx.limpieza.modelo.conexion.ConexionBD;

/**
 * Acceso a datos de la tabla colonia (catálogo de SEPOMEX).
 */
public class ColoniaDAO implements DAO<Colonia> {

    private static final String COLUMNAS = "id, codigo_postal, nombre, tipo_asentamiento";

    @Override
    public void insertar(Colonia colonia) throws SQLException {
        String sql = "INSERT INTO colonia (codigo_postal, nombre, tipo_asentamiento) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, colonia.getCodigoPostal());
            ps.setString(2, colonia.getNombre());
            ps.setString(3, colonia.getTipoAsentamiento());
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    colonia.setId(claves.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Colonia colonia) throws SQLException {
        String sql = "UPDATE colonia SET codigo_postal = ?, nombre = ?, tipo_asentamiento = ? WHERE id = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, colonia.getCodigoPostal());
            ps.setString(2, colonia.getNombre());
            ps.setString(3, colonia.getTipoAsentamiento());
            ps.setInt(4, colonia.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws SQLException {
        try (PreparedStatement ps = conexion().prepareStatement("DELETE FROM colonia WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Colonia buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM colonia WHERE id = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    @Override
    public List<Colonia> listar() throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM colonia ORDER BY nombre, codigo_postal";
        List<Colonia> colonias = new ArrayList<>();
        try (PreparedStatement ps = conexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                colonias.add(mapear(rs));
            }
        }
        return colonias;
    }

    /**
     * Busca colonias cuyo nombre contenga el texto o cuyo código postal
     * empiece con él. Si el texto está vacío, devuelve todas.
     */
    public List<Colonia> buscar(String texto) throws SQLException {
        if (texto == null || texto.isBlank()) {
            return listar();
        }
        String buscado = texto.trim();
        String sql = "SELECT " + COLUMNAS + " FROM colonia"
                + " WHERE nombre LIKE ? OR codigo_postal LIKE ?"
                + " ORDER BY nombre, codigo_postal";
        List<Colonia> colonias = new ArrayList<>();
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            // % significa "cualquier texto": el nombre lo contiene y el código postal empieza con él
            ps.setString(1, "%" + buscado + "%");
            ps.setString(2, buscado + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    colonias.add(mapear(rs));
                }
            }
        }
        return colonias;
    }

    private Colonia mapear(ResultSet rs) throws SQLException {
        return new Colonia(
                rs.getInt("id"),
                rs.getString("codigo_postal"),
                rs.getString("nombre"),
                rs.getString("tipo_asentamiento"));
    }

    private Connection conexion() throws SQLException {
        return ConexionBD.getInstancia().getConexion();
    }
}
