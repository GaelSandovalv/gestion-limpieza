package mx.limpieza.controlador;

import java.sql.SQLException;

/**
 * Convierte los errores de MySQL en mensajes que el usuario pueda entender.
 */
public final class MensajesBD {

    // Códigos de error de MySQL
    private static final int ERROR_DUPLICADO = 1062;
    private static final int ERROR_REGISTRO_RELACIONADO = 1451;
    private static final int ERROR_ACCESO_DENEGADO = 1045;
    private static final int ERROR_BASE_NO_EXISTE = 1049;

    private MensajesBD() {
    }

    public static String traducir(SQLException e) {
        switch (e.getErrorCode()) {
            case ERROR_DUPLICADO:
                return "Ya existe un registro con esos datos.";
            case ERROR_REGISTRO_RELACIONADO:
                return "No se puede eliminar porque tiene registros relacionados.";
            case ERROR_ACCESO_DENEGADO:
                return "MySQL rechazó el usuario o la contraseña. Revise db.usuario y db.contrasena en src/main/resources/db.properties.";
            case ERROR_BASE_NO_EXISTE:
                return "No existe la base de datos gestion_limpieza. Ejecute el script bd/01_esquema.sql.";
            default:
                break;
        }
        String estado = e.getSQLState();
        if (estado != null && estado.startsWith("08")) {
            return "No se pudo conectar a la base de datos. Verifique que MySQL esté encendido.";
        }
        return "Error en la base de datos: " + e.getMessage();
    }
}
