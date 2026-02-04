package com.hotelapp.dao;

import com.hotelapp.database.Database;
import com.hotelapp.model.Guest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * GuestDAO — CRUD для гостей.
 */
public class GuestDAO {

    public List<Guest> findAll() {
        List<Guest> guests = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT * FROM guests ORDER BY name")) {
            while (rs.next()) {
                guests.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка чтения guests: " + e.getMessage());
        }
        return guests;
    }

    public Optional<Guest> findById(int id) {
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM guests WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка поиска гостя: " + e.getMessage());
        }
        return Optional.empty();
    }

    public void insert(Guest guest) throws SQLException {
        String sql = "INSERT INTO guests(name, passport, phone) VALUES(?,?,?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, guest.getName());
            ps.setString(2, guest.getPassport());
            ps.setString(3, guest.getPhone());
            ps.executeUpdate();
        }
    }

    public void update(Guest guest) throws SQLException {
        String sql = "UPDATE guests SET name=?, passport=?, phone=? WHERE id=?";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, guest.getName());
            ps.setString(2, guest.getPassport());
            ps.setString(3, guest.getPhone());
            ps.setInt(4, guest.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM guests WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Guest mapRow(ResultSet rs) throws SQLException {
        Guest guest = new Guest();
        guest.setId(rs.getInt("id"));
        guest.setName(rs.getString("name"));
        guest.setPassport(rs.getString("passport"));
        guest.setPhone(rs.getString("phone"));
        return guest;
    }
}

