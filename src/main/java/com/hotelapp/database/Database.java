package com.hotelapp.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDate;
import java.util.stream.Collectors;


public final class Database {

    private static final Logger logger = LoggerFactory.getLogger(Database.class);
    private static final String DB_URL = "jdbc:sqlite:hotel.db";
    private static boolean initialized = false;

    private Database() {
    }


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }


    public static synchronized void initialize() {
        if (initialized) {
            logger.debug("База данных уже инициализирована");
            return;
        }
        try (Connection connection = getConnection()) {
            logger.info("Инициализация базы данных: {}", DB_URL);
            runSchema(connection);
            seedIfEmpty(connection);
            initialized = true;
            logger.info("База данных успешно инициализирована");
        } catch (SQLException | IOException e) {
            logger.error("Ошибка инициализации БД", e);
            System.err.println("Ошибка инициализации БД: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runSchema(Connection connection) throws IOException, SQLException {
        logger.debug("Загрузка schema.sql");
        try (InputStream inputStream = Database.class.getResourceAsStream("/sql/schema.sql")) {
            if (inputStream == null) {
                logger.error("Файл schema.sql не найден в /sql/schema.sql");
                throw new IOException("Не найден файл schema.sql в /sql/schema.sql");
            }
            String sql = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            try (Statement statement = connection.createStatement()) {
                for (String chunk : sql.split(";")) {
                    String trimmed = chunk.trim();
                    if (!trimmed.isEmpty()) {
                        statement.execute(trimmed);
                    }
                }
            }
        }
    }

    private static void seedIfEmpty(Connection connection) throws SQLException {
        if (isTableEmpty(connection, "users")) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users(username, password_hash, role) VALUES(?,?,?)")) {
                ps.setString(1, "admin");
                ps.setString(2, "admin123"); // пароль
                ps.setString(3, "ADMIN");
                ps.executeUpdate();
            }
        }

        if (isTableEmpty(connection, "rooms")) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO rooms(number, type, price, capacity, status) VALUES(?,?,?,?,?)")) {
                Object[][] rooms = {
                        {"101", "Эконом", 80.0, 1, "FREE"},
                        {"102", "Эконом", 80.0, 2, "FREE"},
                        {"201", "Стандарт", 120.0, 2, "FREE"},
                        {"202", "Стандарт", 120.0, 3, "FREE"},
                        {"301", "Премиум", 200.0, 2, "FREE"},
                        {"302", "Премиум", 200.0, 3, "FREE"},
                        {"401", "Люкс", 350.0, 2, "FREE"},
                        {"402", "Люкс", 350.0, 4, "FREE"}
                };
                for (Object[] room : rooms) {
                    ps.setString(1, (String) room[0]);
                    ps.setString(2, (String) room[1]);
                    ps.setDouble(3, (Double) room[2]);
                    ps.setInt(4, (Integer) room[3]);
                    ps.setString(5, (String) room[4]);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }

        if (isTableEmpty(connection, "guests")) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO guests(name, passport, phone) VALUES(?,?,?)")) {
                Object[][] guests = {
                        {"Иван Петров", "MP123456", "+375291112233"},
                        {"Анна Сидорова", "MP654321", "+375333334455"}
                };
                for (Object[] guest : guests) {
                    ps.setString(1, (String) guest[0]);
                    ps.setString(2, (String) guest[1]);
                    ps.setString(3, (String) guest[2]);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }

        if (isTableEmpty(connection, "bookings")) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO bookings(room_id, guest_id, check_in_date, check_out_date, status) VALUES(?,?,?,?,?)")) {
                LocalDate start = LocalDate.now().plusDays(1);
                LocalDate end = start.plusDays(3);
                ps.setInt(1, 1);
                ps.setInt(2, 1);
                ps.setString(3, start.toString());
                ps.setString(4, end.toString());
                ps.setString(5, "BOOKED");
                ps.executeUpdate();
            }
        }
    }

    private static boolean isTableEmpty(Connection connection, String tableName) throws SQLException {
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            return rs.next() && rs.getInt(1) == 0;
        }
    }
}

