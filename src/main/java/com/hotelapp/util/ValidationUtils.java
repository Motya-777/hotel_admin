package com.hotelapp.util;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * ValidationUtils — централизованная валидация данных для всех форм.
 * Все методы возвращают результат валидации и сообщение об ошибке (если есть).
 */
public class ValidationUtils {

    // Регулярные выражения для валидации
    private static final Pattern ROOM_NUMBER_PATTERN = Pattern.compile("^[0-9]{2,4}([A-Za-zА-Яа-я]|-[0-9]{2})?$");
    private static final Pattern PASSPORT_PATTERN = Pattern.compile("^[A-Z]{2}[0-9]{7}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+375\\s?(17|25|29|33|44)\\s?[0-9]{3}\\s?[0-9]{2}\\s?[0-9]{2}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[А-Яа-яA-Za-zЎўІіЁёҐґЇї\\s]{2,}$");


    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;

        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }


    public static ValidationResult validateRoomNumber(String number) {
        if (number == null || number.trim().isEmpty()) {
            return ValidationResult.error("Номер комнаты не может быть пустым");
        }
        String trimmed = number.trim();
        if (!ROOM_NUMBER_PATTERN.matcher(trimmed).matches()) {
            return ValidationResult.error("Неверный формат номера. Примеры: 101, 203, 101A, 12-03");
        }
        return ValidationResult.success();
    }

    /**
     * Валидация паспорта (Беларусь).
     * Формат: две латинские буквы + 7 цифр.
     * Примеры: AB1234567, MP7654321
     */
    public static ValidationResult validatePassport(String passport) {
        if (passport == null || passport.trim().isEmpty()) {
            return ValidationResult.error("Паспорт не может быть пустым");
        }
        String trimmed = passport.trim().toUpperCase();
        if (!PASSPORT_PATTERN.matcher(trimmed).matches()) {
            return ValidationResult.error("Неверный формат паспорта. Формат: две латинские буквы + 7 цифр (например: AB1234567)");
        }
        return ValidationResult.success();
    }

    /**
     * Валидация номера телефона (Беларусь).
     * Формат: +375 XX XXX XX XX
     * Примеры: +375 17 123 45 67, +375 29 1234567
     */
    public static ValidationResult validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return ValidationResult.error("Телефон не может быть пустым");
        }
        String trimmed = phone.trim();
        if (!PHONE_PATTERN.matcher(trimmed).matches()) {
            return ValidationResult.error("Неверный формат телефона. Формат: +375 XX XXX XX XX (например: +375 17 123 45 67)");
        }
        return ValidationResult.success();
    }

    /**
     * Валидация имени гостя.
     * Только буквы (русские, белорусские, латиница), минимум 2 символа.
     */
    public static ValidationResult validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return ValidationResult.error("Имя не может быть пустым");
        }
        String trimmed = name.trim();
        if (trimmed.length() < 2) {
            return ValidationResult.error("Имя должно содержать минимум 2 символа");
        }
        if (!NAME_PATTERN.matcher(trimmed).matches()) {
            return ValidationResult.error("Имя должно содержать только буквы");
        }
        return ValidationResult.success();
    }

    /**
     * Валидация цены.
     * Должна быть положительным числом.
     */
    public static ValidationResult validatePrice(String priceStr) {
        if (priceStr == null || priceStr.trim().isEmpty()) {
            return ValidationResult.error("Цена не может быть пустой");
        }
        try {
            double price = Double.parseDouble(priceStr.trim());
            if (price <= 0) {
                return ValidationResult.error("Цена должна быть положительным числом");
            }
            return ValidationResult.success();
        } catch (NumberFormatException e) {
            return ValidationResult.error("Цена должна быть числом");
        }
    }

    /**
     * Валидация вместимости комнаты.
     * Должна быть целым числом от 1 до 5.
     */
    public static ValidationResult validateCapacity(String capacityStr) {
        if (capacityStr == null || capacityStr.trim().isEmpty()) {
            return ValidationResult.error("Вместимость не может быть пустой");
        }
        try {
            int capacity = Integer.parseInt(capacityStr.trim());
            return validateCapacity(capacity);
        } catch (NumberFormatException e) {
            return ValidationResult.error("Вместимость должна быть целым числом");
        }
    }

    /**
     * Валидация вместимости комнаты (int версия).
     * Должна быть от 1 до 5.
     */
    public static ValidationResult validateCapacity(int capacity) {
        if (capacity < 1) {
            return ValidationResult.error("Вместимость должна быть не менее 1");
        }
        if (capacity > 5) {
            return ValidationResult.error("Вместимость должна быть не более 5");
        }
        return ValidationResult.success();
    }

    /**
     * Валидация дат бронирования.
     * Check-out должен быть позже check-in.
     * Check-in не может быть раньше сегодняшнего дня.
     */
    public static ValidationResult validateBookingDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null) {
            return ValidationResult.error("Дата заезда не может быть пустой");
        }
        if (checkOut == null) {
            return ValidationResult.error("Дата выезда не может быть пустой");
        }
        LocalDate today = LocalDate.now();
        if (checkIn.isBefore(today)) {
            return ValidationResult.error("Дата заселения не может быть в прошлом");
        }
        if (!checkIn.isBefore(checkOut)) {
            return ValidationResult.error("Дата выезда должна быть позже даты заселения");
        }
        return ValidationResult.success();
    }
}

