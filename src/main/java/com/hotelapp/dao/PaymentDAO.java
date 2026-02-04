package com.hotelapp.dao;

import com.hotelapp.database.Database;
import com.hotelapp.model.Payment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PaymentDAO — хранит информацию об оплатах.
 */
public class PaymentDAO {

    public List<Payment> findByBooking(int bookingId) {
        List<Payment> payments = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM payments WHERE booking_id=?")) {
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                payments.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка чтения payments: " + e.getMessage());
        }
        return payments;
    }

    public void insert(Payment payment) throws SQLException {
        String sql = "INSERT INTO payments(booking_id, amount, paid) VALUES(?,?,?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, payment.getBookingId());
            ps.setDouble(2, payment.getAmount());
            ps.setInt(3, payment.isPaid() ? 1 : 0);
            ps.executeUpdate();
        }
    }

    public void markPaid(int bookingId) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE payments SET paid=1 WHERE booking_id=?")) {
            ps.setInt(1, bookingId);
            ps.executeUpdate();
        }
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getInt("id"));
        payment.setBookingId(rs.getInt("booking_id"));
        payment.setAmount(rs.getDouble("amount"));
        payment.setPaid(rs.getInt("paid") == 1);
        return payment;
    }
}

