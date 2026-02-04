package com.hotelapp.service;

import com.hotelapp.dao.RoomDAO;
import com.hotelapp.model.Room;
import com.hotelapp.model.Room.RoomStatus;

import java.sql.SQLException;
import java.util.List;


public class RoomService {

    private final RoomDAO roomDAO;

    public RoomService(RoomDAO roomDAO) {
        this.roomDAO = roomDAO;
    }

    public List<Room> getAllRooms() {
        return roomDAO.findAll();
    }

    public void saveRoom(Room room) {
        validate(room);
        try {
            if (room.getId() == 0) {
                roomDAO.insert(room);
            } else {
                roomDAO.update(room);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось сохранить комнату: " + e.getMessage(), e);
        }
    }

    public void deleteRoom(Room room) {
        if (room == null || room.getId() == 0) {
            return;
        }
        try {
            roomDAO.delete(room.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось удалить комнату: " + e.getMessage(), e);
        }
    }

    public void updateStatus(int id, RoomStatus status) {
        try {
            roomDAO.updateStatus(id, status);
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось обновить статус комнаты", e);
        }
    }

    private void validate(Room room) {
        if (room.getNumber() == null || room.getNumber().isBlank()) {
            throw new IllegalArgumentException("Номер комнаты обязателен");
        }
        if (room.getPrice() <= 0) {
            throw new IllegalArgumentException("Цена должна быть > 0");
        }
        if (room.getCapacity() <= 0) {
            throw new IllegalArgumentException("Вместимость должна быть > 0");
        }
        if (room.getStatus() == null) {
            room.setStatus(RoomStatus.FREE);
        }
    }
}

