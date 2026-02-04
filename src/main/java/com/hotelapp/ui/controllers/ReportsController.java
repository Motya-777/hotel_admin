package com.hotelapp.ui.controllers;

import com.hotelapp.MainApp;
import com.hotelapp.model.Booking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * ReportsController — простой отчёт по всем броням.
 */
public class ReportsController {

    @FXML
    private TableView<Booking> reportTable;
    @FXML
    private TableColumn<Booking, String> guestColumn;
    @FXML
    private TableColumn<Booking, String> roomColumn;
    @FXML
    private TableColumn<Booking, String> statusColumn;

    private final ObservableList<Booking> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        guestColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getGuestName()));
        roomColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getRoomNumber()));
        statusColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus().name()));
        reportTable.setItems(data);
        refresh();
    }

    private void refresh() {
        data.setAll(MainApp.getBookingService().getAllBookings());
    }
}

