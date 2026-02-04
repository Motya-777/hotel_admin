package com.hotelapp.service;

import com.hotelapp.dao.BookingDAO;
import com.hotelapp.dao.GuestDAO;
import com.hotelapp.dao.PaymentDAO;
import com.hotelapp.dao.RoomDAO;
import com.hotelapp.model.Booking;
import com.hotelapp.model.Booking.BookingStatus;
import com.hotelapp.model.Room;
import com.hotelapp.model.Room.RoomStatus;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class BookingService {

    private final BookingDAO bookingDAO;
    private final RoomDAO roomDAO;
    private final GuestDAO guestDAO;
    private final PaymentDAO paymentDAO;

    public BookingService(BookingDAO bookingDAO, RoomDAO roomDAO, GuestDAO guestDAO, PaymentDAO paymentDAO) {
        this.bookingDAO = bookingDAO;
        this.roomDAO = roomDAO;
        this.guestDAO = guestDAO;
        this.paymentDAO = paymentDAO;
    }

    public List<Booking> getAllBookings() {
        return bookingDAO.findAll();
    }

    public List<Room> getAvailableRooms(LocalDate start, LocalDate end) {
        if (start == null || end == null || !start.isBefore(end)) {
            return List.of();
        }
        return roomDAO.findAll().stream()
                .filter(room -> room.getStatus() == Room.RoomStatus.FREE) // Только свободные номера
                .filter(room -> !bookingDAO.hasOverlaps(room.getId(), start, end)) // Без пересечений с бронями
                .collect(Collectors.toList());
    }

    public void createBooking(int guestId, int roomId, LocalDate start, LocalDate end) {
        if (start == null || end == null || !start.isBefore(end)) {
            throw new IllegalArgumentException("Дата выезда должна быть позже даты заезда");
        }
        if (start.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Нельзя бронировать задним числом");
        }
        if (roomDAO.findById(roomId).isEmpty()) {
            throw new IllegalArgumentException("Номер не найден");
        }
        if (guestDAO.findById(guestId).isEmpty()) {
            throw new IllegalArgumentException("Гость не найден");
        }
        if (bookingDAO.hasOverlaps(roomId, start, end)) {
            throw new IllegalStateException("Комната занята в выбранные даты");
        }
        Booking booking = new Booking();
        booking.setRoomId(roomId);
        booking.setGuestId(guestId);
        booking.setCheckInDate(start);
        booking.setCheckOutDate(end);
        booking.setStatus(BookingStatus.BOOKED);
        try {
            bookingDAO.insert(booking);
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось создать бронь", e);
        }
    }

    public void checkIn(int bookingId) {
        Booking booking = getBookingOrThrow(bookingId);
        changeBookingStatus(booking, BookingStatus.CHECKED_IN, RoomStatus.OCCUPIED);
    }

    public double checkOut(int bookingId, boolean paid) {
        Booking booking = getBookingOrThrow(bookingId);
        changeBookingStatus(booking, BookingStatus.CHECKED_OUT, RoomStatus.FREE);
        double total = calculateTotalCost(booking);
        try {
            paymentDAO.insert(new com.hotelapp.model.Payment(bookingId, total, paid));
            if (paid) {
                paymentDAO.markPaid(bookingId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось сохранить оплату", e);
        }
        return total;
    }

    private Booking getBookingOrThrow(int bookingId) {
        return bookingDAO.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Бронь не найдена"));
    }

    private void changeBookingStatus(Booking booking, BookingStatus status, RoomStatus roomStatus) {
        try {
            bookingDAO.updateStatus(booking.getId(), status);
            roomDAO.updateStatus(booking.getRoomId(), roomStatus);
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось обновить статус", e);
        }
    }

    public double calculateTotalCost(Booking booking) {
        Optional<Room> roomOpt = roomDAO.findById(booking.getRoomId());
        if (roomOpt.isEmpty()) {
            return 0;
        }
        long nights = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        return nights * roomOpt.get().getPrice();
    }

    public List<Booking> getBookingsForCheckIn(LocalDate date) {
        return bookingDAO.findForCheckIn(date);
    }

    public List<Booking> getBookingsForCheckOut() {
        return bookingDAO.findForCheckOut();
    }
}

