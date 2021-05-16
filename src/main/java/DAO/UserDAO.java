package DAO;

import Entity.VKUser;

import java.sql.*;
import java.util.*;


public class UserDAO implements DAO<VKUser> {

    private static final String connectionURL = "jdbc:mysql://localhost/av";
    private String query;
    public static List<VKUser> users = new ArrayList<>();

    Properties properties = new Properties();

    public UserDAO() throws SQLException {
        getAll();
    }

    @Override
    public void add(VKUser user) throws SQLException {

        properties.setProperty("user", "root");
        properties.setProperty("password", "4241");
        properties.setProperty("useSSL", "false");
        properties.setProperty("autoReconnect", "true");
        properties.setProperty("serverTimezone","UTC");

        Connection connection = DriverManager.getConnection(connectionURL, properties);
        Statement statement = connection.createStatement();

        query = "INSERT INTO user(id, car, last_message) VALUES('" + user.getId() + "', '" + user.getCar() + "', '" + user.getLastMessage() + "')";
        statement.executeUpdate(query);

        users.add(user);
        connection.close();
    }

    public void setCar(VKUser user, String car) throws SQLException {

        properties.setProperty("user", "root");
        properties.setProperty("password", "4241");
        properties.setProperty("useSSL", "false");
        properties.setProperty("autoReconnect", "true");
        properties.setProperty("serverTimezone","UTC");

        Connection connection = DriverManager.getConnection(connectionURL, properties);
        Statement statement = connection.createStatement();

        try {
            query = "UPDATE user SET car='"+ car +"' WHERE id="+ user.getId() +"";
            statement.executeUpdate(query);
            users.get(users.indexOf(user)).setCar(car);
        }
        catch (SQLException ex) {
            System.out.print(ex.getMessage());
        }

        connection.close();
    }

    public void setLastMessage(VKUser user, String message) throws SQLException {

        properties.setProperty("user", "root");
        properties.setProperty("password", "4241");
        properties.setProperty("useSSL", "false");
        properties.setProperty("autoReconnect", "true");
        properties.setProperty("serverTimezone","UTC");

        Connection connection = DriverManager.getConnection(connectionURL, properties);
        Statement statement = connection.createStatement();

        try {
            query = "UPDATE user SET last_message='"+ message +"' WHERE id="+ user.getId() +"";
            statement.executeUpdate(query);
            user.setLastMessage(message);
            users.get(users.indexOf(user)).setLastMessage(message);
        }
        catch (SQLException ex) {
            System.out.print(ex.getMessage());
        }

        connection.close();
    }

    @Override
    public void delete(VKUser user) throws SQLException {
        properties.setProperty("user", "root");
        properties.setProperty("password", "4241");
        properties.setProperty("useSSL", "false");
        properties.setProperty("autoReconnect", "true");
        properties.setProperty("serverTimezone","UTC");

        Connection connection = DriverManager.getConnection(connectionURL, properties);
        Statement statement = connection.createStatement();

        query = "DELETE FROM user WHERE id='" + user.getId() + "'";
        statement.executeUpdate(query);
        users.remove(user);
        connection.close();
    }

    @Override
    public void getAll() throws SQLException {
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
        String message;
        while (rs.next()) {
            id = rs.getInt("id");
            car = rs.getString("car");
            message = rs.getString("car");
            user = new VKUser(id, car, message);
            users.add(user);
        }

        connection.close();
    }
}
