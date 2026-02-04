package com.hotelapp.dao;

import com.hotelapp.database.Database;
import com.hotelapp.model.Booking;
import com.hotelapp.model.Booking.BookingStatus;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class BookingDAO {

    public List<Booking> findAll() {
        String sql = """
                SELECT b.*, r.number AS room_number, g.name AS guest_name
                FROM bookings b
                JOIN rooms r ON b.room_id = r.id
                JOIN guests g ON b.guest_id = g.id
                ORDER BY b.check_in_date
                """;
        List<Booking> bookings = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                bookings.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка чтения bookings: " + e.getMessage());
        }
        return bookings;
    }

    public List<Booking> findByStatus(BookingStatus status) {
        String sql = """
                SELECT b.*, r.number AS room_number, g.name AS guest_name
                FROM bookings b
                JOIN rooms r ON b.room_id = r.id
                JOIN guests g ON b.guest_id = g.id
                WHERE b.status=?
                ORDER BY b.check_in_date
                """;
        List<Booking> result = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка выборки бронирований: " + e.getMessage());
        }
        return result;
    }

    public Optional<Booking> findById(int id) {
        String sql = """
                SELECT b.*, r.number AS room_number, g.name AS guest_name
                FROM bookings b
                JOIN rooms r ON b.room_id = r.id
                JOIN guests g ON b.guest_id = g.id
                WHERE b.id=?
                """;
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка поиска брони: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Booking> findForCheckIn(LocalDate date) {
        String sql = """
                SELECT b.*, r.number AS room_number, g.name AS guest_name
                FROM bookings b
                JOIN rooms r ON b.room_id = r.id
                JOIN guests g ON b.guest_id = g.id
                WHERE b.status='BOOKED' AND b.check_in_date<=?
                ORDER BY b.check_in_date
                """;
        List<Booking> result = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, date.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка поиска на заселение: " + e.getMessage());
        }
        return result;
    }

    public List<Booking> findForCheckOut() {
        return findByStatus(BookingStatus.CHECKED_IN);
    }

    public void insert(Booking booking) throws SQLException {
        String sql = "INSERT INTO bookings(room_id, guest_id, check_in_date, check_out_date, status) VALUES(?,?,?,?,?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, booking.getRoomId());
            ps.setInt(2, booking.getGuestId());
            ps.setString(3, booking.getCheckInDate().toString());
            ps.setString(4, booking.getCheckOutDate().toString());
            ps.setString(5, booking.getStatus().name());
            ps.executeUpdate();
        }
    }

    public void updateStatus(int bookingId, BookingStatus status) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE bookings SET status=? WHERE id=?")) {
            ps.setString(1, status.name());
            ps.setInt(2, bookingId);
            ps.executeUpdate();
        }
    }

    public boolean hasOverlaps(int roomId, LocalDate start, LocalDate end) {
        String sql = """
                SELECT COUNT(*) FROM bookings
                WHERE room_id=?
                  AND status <> 'CHECKED_OUT'
                  AND (date(check_in_date) < date(?) AND date(check_out_date) > date(?))
                """;
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setString(2, end.toString());
            ps.setString(3, start.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка проверки пересечения: " + e.getMessage());
        }
        return true;
    }

    private Booking mapRow(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setId(rs.getInt("id"));
        booking.setRoomId(rs.getInt("room_id"));
        booking.setGuestId(rs.getInt("guest_id"));
        booking.setRoomNumber(rs.getString("room_number"));
        booking.setGuestName(rs.getString("guest_name"));
        booking.setCheckInDate(LocalDate.parse(rs.getString("check_in_date")));
        booking.setCheckOutDate(LocalDate.parse(rs.getString("check_out_date")));
        booking.setStatus(BookingStatus.valueOf(rs.getString("status")));
        return booking;
    }
}

