package com.hotelapp.dao;

import com.hotelapp.database.Database;
import com.hotelapp.model.Room;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Простой тест RoomDAO: проверяем, что после инициализации БД есть хотя бы одна запись.
 */
class RoomDAOTest {

    @BeforeAll
    static void setup() {
        Database.initialize();
    }

    @Test
    void roomsShouldNotBeEmptyAfterSeed() {
        RoomDAO dao = new RoomDAO();
        List<Room> rooms = dao.findAll();
        Assertions.assertFalse(rooms.isEmpty(), "Должны быть тестовые номера");
    }
}

