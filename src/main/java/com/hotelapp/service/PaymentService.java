package com.hotelapp.service;

import com.hotelapp.dao.PaymentDAO;
import com.hotelapp.model.Payment;

import java.sql.SQLException;


public class PaymentService {

    private final PaymentDAO paymentDAO;

    public PaymentService(PaymentDAO paymentDAO) {
        this.paymentDAO = paymentDAO;
    }


    public void createPayment(int bookingId, double amount, boolean paid) {
        Payment payment = new Payment(bookingId, amount, paid);
        try {
            paymentDAO.insert(payment);
            if (paid) {
                paymentDAO.markPaid(bookingId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось сохранить оплату", e);
        }
    }

    public void markAsPaid(int bookingId) {
        try {
            paymentDAO.markPaid(bookingId);
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось пометить как оплачено", e);
        }
    }
}

