package com.hotelapp.dao;

import com.hotelapp.database.Database;
import com.hotelapp.model.Room;
import com.hotelapp.model.Room.RoomStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * RoomDAO — CRUD-операции над таблицей rooms.
 */
public class RoomDAO {

    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT * FROM rooms ORDER BY number")) {
            while (rs.next()) {
                rooms.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка чтения rooms: " + e.getMessage());
        }
        return rooms;
    }

    public Optional<Room> findById(int id) {
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM rooms WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка поиска комнаты: " + e.getMessage());
        }
        return Optional.empty();
    }

    public void insert(Room room) throws SQLException {
        String sql = "INSERT INTO rooms(number, type, price, capacity, status) VALUES(?,?,?,?,?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, room.getNumber());
            ps.setString(2, room.getType());
            ps.setDouble(3, room.getPrice());
            ps.setInt(4, room.getCapacity());
            ps.setString(5, room.getStatus().name());
            ps.executeUpdate();
        }
    }

    public void update(Room room) throws SQLException {
        String sql = "UPDATE rooms SET number=?, type=?, price=?, capacity=?, status=? WHERE id=?";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, room.getNumber());
            ps.setString(2, room.getType());
            ps.setDouble(3, room.getPrice());
            ps.setInt(4, room.getCapacity());
            ps.setString(5, room.getStatus().name());
            ps.setInt(6, room.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM rooms WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void updateStatus(int id, RoomStatus status) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE rooms SET status=? WHERE id=?")) {
            ps.setString(1, status.name());
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    private Room mapRow(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getInt("id"));
        room.setNumber(rs.getString("number"));
        room.setType(rs.getString("type"));
        room.setPrice(rs.getDouble("price"));
        room.setCapacity(rs.getInt("capacity"));
        room.setStatus(RoomStatus.valueOf(rs.getString("status")));
        return room;
    }
}

