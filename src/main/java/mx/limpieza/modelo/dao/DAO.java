package mx.limpieza.modelo.dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Patrón DAO (Data Access Object): define las operaciones CRUD que toda
 * clase de acceso a datos debe implementar. Así los controladores trabajan
 * con objetos y nunca escriben SQL.
 *
 * @param <T> entidad que maneja el DAO
 */
public interface DAO<T> {

    /** Guarda un registro nuevo y le asigna al objeto el id generado. */
    void insertar(T objeto) throws SQLException;

    /** Modifica un registro existente (se identifica por su id). */
    void actualizar(T objeto) throws SQLException;

    /** Elimina el registro con el id indicado. */
    void eliminar(int id) throws SQLException;

    /** Busca un registro por su id; devuelve null si no existe. */
    T buscarPorId(int id) throws SQLException;

    /** Devuelve todos los registros. */
    List<T> listar() throws SQLException;
}
