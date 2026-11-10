package mx.limpieza.modelo.conexion;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Conexión a la base de datos MySQL usando el patrón Singleton.
 * Toda la aplicación comparte una sola instancia (y una sola conexión),
 * que se obtiene con {@link #getInstancia()}.
 */
public final class ConexionBD {

    private static final String ARCHIVO_CONFIGURACION = "/db.properties";
    private static final int SEGUNDOS_VALIDACION = 2;

    // Única instancia de la clase
    private static ConexionBD instancia;

    private final Connection conexion;

    // El constructor es privado para que nadie más pueda crear instancias
    private ConexionBD() throws SQLException {
        Properties configuracion = leerConfiguracion();
        conexion = DriverManager.getConnection(
                configuracion.getProperty("db.url"),
                configuracion.getProperty("db.usuario"),
                configuracion.getProperty("db.contrasena"));
    }

    /**
     * Devuelve la única instancia. Si todavía no existe, o si la conexión
     * se cerró o se perdió, se crea una nueva.
     */
    public static synchronized ConexionBD getInstancia() throws SQLException {
        if (instancia == null || !instancia.conexion.isValid(SEGUNDOS_VALIDACION)) {
            cerrar();
            instancia = new ConexionBD();
        }
        return instancia;
    }

    public Connection getConexion() {
        return conexion;
    }

    /**
     * Cierra la conexión con la base de datos y destruye la instancia.
     */
    public static synchronized void cerrar() {
        if (instancia != null) {
            try {
                instancia.conexion.close();
            } catch (SQLException e) {
                // La conexión ya no se puede usar; solo se descarta
            }
            instancia = null;
        }
    }

    private static Properties leerConfiguracion() throws SQLException {
        Properties configuracion = new Properties();
        try (InputStream entrada = ConexionBD.class.getResourceAsStream(ARCHIVO_CONFIGURACION)) {
            if (entrada == null) {
                throw new SQLException("No se encontró el archivo de configuración db.properties");
            }
            configuracion.load(entrada);
        } catch (IOException e) {
            throw new SQLException("No se pudo leer el archivo db.properties", e);
        }
        return configuracion;
    }
}
