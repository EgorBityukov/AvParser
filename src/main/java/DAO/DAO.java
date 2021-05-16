package DAO;

import java.sql.SQLException;

public interface DAO {

    public void add(int id) throws SQLException;
    public void delete(int id) throws SQLException;
    public void get() throws SQLException;
}
