package com.hotelapp.model;

/**
 * Room — модель номера: номер комнаты, тип, цена, вместимость и статус.
 */
public class Room {

    private int id;
    private String number;
    private String type;
    private double price;
    private int capacity;
    private RoomStatus status;

    public Room() {
    }

    public Room(int id, String number, String type, double price, int capacity, RoomStatus status) {
        this.id = id;
        this.number = number;
        this.type = type;
        this.price = price;
        this.capacity = capacity;
        this.status = status;
    }

    public Room(String number, String type, double price, int capacity, RoomStatus status) {
        this(0, number, type, price, capacity, status);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return number + " (" + type + ")";
    }
    /**
     * RoomStatus — enum статусов комнаты.
     */
    public enum RoomStatus {
        FREE,
        OCCUPIED
    }
}

