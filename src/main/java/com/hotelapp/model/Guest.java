package com.hotelapp.model;


public class Guest {
    private int id;
    private String name;
    private String passport;
    private String phone;

    public Guest() {
    }

    public Guest(int id, String name, String passport, String phone) {
        this.id = id;
        this.name = name;
        this.passport = passport;
        this.phone = phone;
    }

    public Guest(String name, String passport, String phone) {
        this(0, name, passport, phone);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return name;
    }
}

