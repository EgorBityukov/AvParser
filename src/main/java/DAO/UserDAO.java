package DAO;

import Entity.VKUser;

import java.sql.*;
import java.util.*;


public class UserDAO implements DAO {

    private static final String connectionURL = "jdbc:mysql://localhost/av";
    private String query;
    public static List<VKUser> users = new ArrayList<VKUser>();

    Properties properties = new Properties();

    public UserDAO() throws SQLException {
        get();
    }

    @Override
    public void add(int id) throws SQLException {

        properties.setProperty("user", "root");
        properties.setProperty("password", "4241");
        properties.setProperty("useSSL", "false");
        properties.setProperty("autoReconnect", "true");
        properties.setProperty("serverTimezone","UTC");

        Connection connection = DriverManager.getConnection(connectionURL, properties);
        Statement statement = connection.createStatement();

        query = "INSERT INTO user(id) VALUES('" + id + "')";
        statement.executeUpdate(query);

        connection.close();
    }

    public void setCar(int id, String car) throws SQLException {

        properties.setProperty("user", "root");
        properties.setProperty("password", "4241");
        properties.setProperty("useSSL", "false");
        properties.setProperty("autoReconnect", "true");
        properties.setProperty("serverTimezone","UTC");

        Connection connection = DriverManager.getConnection(connectionURL, properties);
        Statement statement = connection.createStatement();

        try {
            query = "UPDATE user SET car='"+ car +"' WHERE id="+ id +"";
            statement.executeUpdate(query);
        }
        catch (SQLException ex) {
            System.out.print(ex.getMessage());
        }

        connection.close();
    }

    @Override
    public void delete(int id) throws SQLException {
        properties.setProperty("user", "root");
        properties.setProperty("password", "4241");
        properties.setProperty("useSSL", "false");
        properties.setProperty("autoReconnect", "true");
        properties.setProperty("serverTimezone","UTC");

        Connection connection = DriverManager.getConnection(connectionURL, properties);
        Statement statement = connection.createStatement();

        query = "DELETE FROM user WHERE id='" + id + "')";
        statement.executeUpdate(query);

        connection.close();
    }

    @Override
    public void get() throws SQLException {
        properties.setProperty("user", "root");
        properties.setProperty("password", "4241");
        properties.setProperty("useSSL", "false");
        properties.setProperty("autoReconnect", "true");
        properties.setProperty("serverTimezone","UTC");

        Connection connection = DriverManager.getConnection(connectionURL, properties);
        Statement statement = connection.createStatement();

        query = "SELECT * FROM user";
        ResultSet rs = statement.executeQuery(query);

        VKUser user;
        int id;
        String car;
        while (rs.next()) {
            id = rs.getInt("id");
            car = rs.getString("car");
            user = new VKUser(id, car, null);
            users.add(user);
        }

        connection.close();
    }
}
