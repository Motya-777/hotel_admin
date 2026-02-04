package com.hotelapp.ui.controllers;

import com.hotelapp.MainApp;
import com.hotelapp.model.Booking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

/**
 * CheckInController — управление заселением гостей.
 */
public class CheckInController {

    @FXML
    private TableView<Booking> checkInTable;
    @FXML
    private TableColumn<Booking, String> guestColumn;
    @FXML
    private TableColumn<Booking, String> roomColumn;
    @FXML
    private TableColumn<Booking, String> dateColumn;
    @FXML
    private DatePicker datePicker;

    private final ObservableList<Booking> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        guestColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getGuestName()));
        roomColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getRoomNumber()));
        dateColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCheckInDate().toString()));

        datePicker.setValue(LocalDate.now());
        datePicker.valueProperty().addListener((obs, o, n) -> refreshData());
        checkInTable.setItems(data);
        refreshData();
    }

    @FXML
    private void handleCheckIn() {
        Booking selected = checkInTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Внимание", "Выберите бронь для заселения");
            return;
        }
        try {
            MainApp.getBookingService().checkIn(selected.getId());
            refreshData();
            showAlert(Alert.AlertType.INFORMATION, "Успех", "Гость успешно заселён в номер " + selected.getRoomNumber());
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", e.getMessage());
        }
    }

    private void refreshData() {
        LocalDate date = datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now();
        data.setAll(MainApp.getBookingService().getBookingsForCheckIn(date));
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

