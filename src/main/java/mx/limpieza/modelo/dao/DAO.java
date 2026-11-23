package mx.limpieza.modelo.dao;

import java.sql.SQLException;
import java.util.List;


public interface DAO<T> {
 /**guarda y le asigna el objeto */
    void insertar(T objeto) throws SQLException;

    void actualizar(T objeto) throws SQLException;

    void eliminar(int id) throws SQLException;

    /**null si no existe */
    T buscarPorId(int id) throws SQLException;

    List<T> listar() throws SQLException;
}
