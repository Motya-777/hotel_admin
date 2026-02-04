package com.hotelapp.dao;

import com.hotelapp.database.Database;
import com.hotelapp.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;


public class UserDAO {

    public Optional<User> findByUsername(String username) {
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE username=?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка чтения пользователя: " + e.getMessage());
        }
        return Optional.empty();
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        return user;
    }
}

