package com.moviereservation.repository;

import com.moviereservation.db.DatabaseManager;
import com.moviereservation.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private final DatabaseManager db = DatabaseManager.getInstance();

    public User login(String username, String password) throws SQLException {
        PreparedStatement ps = db.getConnection().prepareStatement(
            "SELECT * FROM users WHERE username = ? AND password_hash = ?");
        ps.setString(1, username);
        ps.setString(2, DatabaseManager.hashPassword(password));
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            User user = new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("full_name"),
                rs.getString("role")
            );
            rs.close();
            ps.close();
            return user;
        }
        rs.close();
        ps.close();
        return null;
    }

    public boolean signup(String username, String password, String fullName) throws SQLException {
        PreparedStatement ps = db.getConnection().prepareStatement(
            "INSERT INTO users (username, password_hash, full_name, role) VALUES (?, ?, ?, 'USER')");
        ps.setString(1, username);
        ps.setString(2, DatabaseManager.hashPassword(password));
        ps.setString(3, fullName);
        try {
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            ps.close();
            return false;
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        var rs = db.getConnection().createStatement().executeQuery(
            "SELECT id, username, full_name, role FROM users ORDER BY id");
        while (rs.next()) {
            users.add(new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("full_name"),
                rs.getString("role")
            ));
        }
        rs.close();
        return users;
    }

    public void promoteUser(int userId) throws SQLException {
        PreparedStatement ps = db.getConnection().prepareStatement(
            "UPDATE users SET role='ADMIN' WHERE id=? AND role='USER'");
        ps.setInt(1, userId);
        ps.executeUpdate();
        ps.close();
    }

    public void demoteUser(int userId) throws SQLException {
        PreparedStatement ps = db.getConnection().prepareStatement(
            "UPDATE users SET role='USER' WHERE id=? AND role='ADMIN'");
        ps.setInt(1, userId);
        ps.executeUpdate();
        ps.close();
    }
}
