package com.hotelapp.ui.controllers;

import com.hotelapp.MainApp;
import com.hotelapp.model.Booking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * CheckOutController — выселение гостей и учёт оплат.
 */
public class CheckOutController {

    @FXML
    private TableView<Booking> checkOutTable;
    @FXML
    private TableColumn<Booking, String> guestColumn;
    @FXML
    private TableColumn<Booking, String> roomColumn;
    @FXML
    private TableColumn<Booking, String> statusColumn;
    @FXML
    private CheckBox paidCheck;

    private final ObservableList<Booking> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        guestColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getGuestName()));
        roomColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getRoomNumber()));
        statusColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus().name()));
        checkOutTable.setItems(data);
        refreshData();
    }

    @FXML
    private void handleCheckOut() {
        Booking selected = checkOutTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Внимание", "Выберите бронь для выселения");
            return;
        }
        try {
            boolean paid = paidCheck.isSelected();
            double total = MainApp.getBookingService().checkOut(selected.getId(), paid);
            refreshData();
            String message = String.format("Выселение завершено.\nСумма к оплате: %.2f руб.\nСтатус оплаты: %s", 
                    total, paid ? "Оплачено" : "Не оплачено");
            showAlert(Alert.AlertType.INFORMATION, "Успех", message);
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", e.getMessage());
        }
    }

    private void refreshData() {
        data.setAll(MainApp.getBookingService().getBookingsForCheckOut());
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

