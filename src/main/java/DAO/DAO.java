package DAO;

import java.sql.SQLException;

public interface DAO<T> {

    void add(T t) throws SQLException;
    void delete(T t) throws SQLException;
    void getAll() throws SQLException;
}
