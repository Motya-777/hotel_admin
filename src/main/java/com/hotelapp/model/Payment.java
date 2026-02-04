package com.hotelapp.model;


public class Payment {
    private int id;
    private int bookingId;
    private double amount;
    private boolean paid;

    public Payment() {
    }

    public Payment(int id, int bookingId, double amount, boolean paid) {
        this.id = id;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paid = paid;
    }

    public Payment(int bookingId, double amount, boolean paid) {
        this(0, bookingId, amount, paid);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}

