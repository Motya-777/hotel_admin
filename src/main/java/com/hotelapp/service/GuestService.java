package com.hotelapp.service;

import com.hotelapp.dao.GuestDAO;
import com.hotelapp.model.Guest;

import java.sql.SQLException;
import java.util.List;


public class GuestService {

    private final GuestDAO guestDAO;

    public GuestService(GuestDAO guestDAO) {
        this.guestDAO = guestDAO;
    }

    public List<Guest> getAllGuests() {
        return guestDAO.findAll();
    }

    public void saveGuest(Guest guest) {
        validate(guest);
        try {
            if (guest.getId() == 0) {
                guestDAO.insert(guest);
            } else {
                guestDAO.update(guest);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось сохранить гостя: " + e.getMessage(), e);
        }
    }

    public void deleteGuest(Guest guest) {
        if (guest == null || guest.getId() == 0) {
            return;
        }
        try {
            guestDAO.delete(guest.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось удалить гостя", e);
        }
    }

    private void validate(Guest guest) {
        if (guest.getName() == null || guest.getName().isBlank()) {
            throw new IllegalArgumentException("Имя гостя обязательно");
        }
        if (guest.getPassport() == null || guest.getPassport().isBlank()) {
            throw new IllegalArgumentException("Номер паспорта обязателен");
        }
    }
}

