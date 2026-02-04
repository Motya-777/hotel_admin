package com.hotelapp.ui.controllers;

import com.hotelapp.MainApp;
import com.hotelapp.model.Booking;
import com.hotelapp.model.Room;
import com.hotelapp.model.Guest;
import com.hotelapp.util.ValidationUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;


public class BookingsController {

    @FXML
    private TableView<Booking> bookingsTable;
    @FXML
    private TableColumn<Booking, String> guestColumn;
    @FXML
    private TableColumn<Booking, String> roomColumn;
    @FXML
    private TableColumn<Booking, String> checkInColumn;
    @FXML
    private TableColumn<Booking, String> checkOutColumn;
    @FXML
    private TableColumn<Booking, String> statusColumn;

    @FXML
    private ComboBox<Guest> guestCombo;
    @FXML
    private ComboBox<Room> roomCombo;
    @FXML
    private DatePicker checkInPicker;
    @FXML
    private DatePicker checkOutPicker;

    private final ObservableList<Booking> bookingsData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        guestColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getGuestName()));
        roomColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRoomNumber()));
        checkInColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCheckInDate().toString()));
        checkOutColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCheckOutDate().toString()));
        statusColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus().name()));

        bookingsTable.setItems(bookingsData);
        guestCombo.setItems(FXCollections.observableArrayList(MainApp.getGuestService().getAllGuests()));
        configureGuestCombo();
        configureRoomCombo();
        
        checkInPicker.valueProperty().addListener((obs, o, n) -> updateRoomChoices());
        checkOutPicker.valueProperty().addListener((obs, o, n) -> updateRoomChoices());
        refreshBookings();
    }

    @FXML
    private void handleCreateBooking() {
        Guest guest = guestCombo.getSelectionModel().getSelectedItem();
        Room room = roomCombo.getSelectionModel().getSelectedItem();
        LocalDate start = checkInPicker.getValue();
        LocalDate end = checkOutPicker.getValue();
        
        if (guest == null) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите гостя");
            return;
        }
        
        if (room == null) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите номер");
            return;
        }
        
        ValidationUtils.ValidationResult datesResult = ValidationUtils.validateBookingDates(start, end);
        if (!datesResult.isValid()) {
            showAlert(Alert.AlertType.ERROR, "Ошибка валидации", datesResult.getErrorMessage());
            return;
        }
        
        try {
            MainApp.getBookingService().createBooking(guest.getId(), room.getId(), start, end);
            refreshBookings();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Успех", "Бронирование успешно создано");
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", e.getMessage());
        }
    }

    private void refreshBookings() {
        bookingsData.setAll(MainApp.getBookingService().getAllBookings());
        guestCombo.setItems(FXCollections.observableArrayList(MainApp.getGuestService().getAllGuests()));
        updateRoomChoices();
    }

    private void updateRoomChoices() {
        LocalDate start = checkInPicker.getValue();
        LocalDate end = checkOutPicker.getValue();
        if (start == null || end == null || !start.isBefore(end)) {
            roomCombo.getItems().clear();
            roomCombo.setPromptText("Сначала выберите даты заезда и выезда");
            return;
        }
        java.util.List<Room> availableRooms = MainApp.getBookingService().getAvailableRooms(start, end);
        if (availableRooms.isEmpty()) {
            roomCombo.getItems().clear();
            roomCombo.setPromptText("Нет свободных номеров на выбранные даты");
        } else {
            roomCombo.setItems(FXCollections.observableArrayList(availableRooms));
            roomCombo.setPromptText("Выберите номер (" + availableRooms.size() + " доступно)");
        }
    }

    private void clearForm() {
        guestCombo.getSelectionModel().clearSelection();
        roomCombo.getSelectionModel().clearSelection();
        roomCombo.setPromptText("Выберите номер");
        checkInPicker.setValue(null);
        checkOutPicker.setValue(null);
    }

    private void configureGuestCombo() {
        guestCombo.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Guest guest, boolean empty) {
                super.updateItem(guest, empty);
                if (empty || guest == null) {
                    setText(null);
                } else {
                    setText(guest.getName() + " (" + guest.getPassport() + ")");
                }
            }
        });

        guestCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Guest guest, boolean empty) {
                super.updateItem(guest, empty);
                if (empty || guest == null) {
                    setText("Выберите гостя");
                } else {
                    setText(guest.getName());
                }
            }
        });
    }

    private void configureRoomCombo() {
        roomCombo.setPlaceholder(new Label("Свободные номера отсутствуют"));
        roomCombo.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Room room, boolean empty) {
                super.updateItem(room, empty);
                if (empty || room == null) {
                    setText(null);
                } else {
                    String text = String.format("№%s • %s • %.0f руб. • %d мест",
                            room.getNumber(),
                            room.getType(),
                            room.getPrice(),
                            room.getCapacity());
                    setText(text);
                }
            }
        });
        roomCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Room room, boolean empty) {
                super.updateItem(room, empty);
                if (empty || room == null) {
                    setText("Выберите номер");
                } else {
                    setText("№" + room.getNumber() + " - " + room.getType());
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

