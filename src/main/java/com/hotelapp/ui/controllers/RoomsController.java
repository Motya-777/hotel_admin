package com.hotelapp.ui.controllers;

import com.hotelapp.MainApp;
import com.hotelapp.model.Room;
import com.hotelapp.model.Room.RoomStatus;
import com.hotelapp.util.ValidationUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

/**
 * RoomsController — CRUD по номерам в таблице с валидацией.
 */
public class RoomsController {

    @FXML
    private TableView<Room> roomsTable;
    @FXML
    private TableColumn<Room, String> numberColumn;
    @FXML
    private TableColumn<Room, String> typeColumn;
    @FXML
    private TableColumn<Room, Number> priceColumn;
    @FXML
    private TableColumn<Room, Number> capacityColumn;
    @FXML
    private TableColumn<Room, String> statusColumn;

    @FXML
    private TextField numberField;
    @FXML
    private ComboBox<String> typeCombo;
    @FXML
    private TextField priceField;
    @FXML
    private TextField capacityField;
    @FXML
    private ComboBox<RoomStatus> statusCombo;
    @FXML
    private Label errorLabel;

    // Фиксированные типы номеров
    private static final String[] ROOM_TYPES = {"Эконом", "Стандарт", "Премиум", "Люкс"};

    private final ObservableList<Room> roomsData = FXCollections.observableArrayList();
    private Room selectedRoom;

    @FXML
    private void initialize() {
        numberColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNumber()));
        typeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getType()));
        priceColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPrice()));
        capacityColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCapacity()));
        statusColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus().name()));

        roomsTable.setItems(roomsData);
        roomsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            selectedRoom = newV;
            populateForm(newV);
            clearError();
        });
        
        // Инициализация ComboBox для типов номеров
        typeCombo.getItems().setAll(ROOM_TYPES);
        typeCombo.setPromptText("Выберите тип номера");
        
        statusCombo.getItems().setAll(RoomStatus.values());
        statusCombo.setValue(RoomStatus.FREE);
        refreshTable();
        
        // Валидация в реальном времени
        numberField.textProperty().addListener((obs, oldV, newV) -> validateRoomNumber());
        priceField.textProperty().addListener((obs, oldV, newV) -> validatePrice());
        capacityField.textProperty().addListener((obs, oldV, newV) -> validateCapacity());
    }

    private void populateForm(Room room) {
        if (room == null) {
            clearForm();
            return;
        }
        numberField.setText(room.getNumber());
        // Устанавливаем тип из ComboBox, если он есть в списке
        String roomType = room.getType();
        if (roomType != null && typeCombo.getItems().contains(roomType)) {
            typeCombo.setValue(roomType);
        } else {
            typeCombo.setValue(null);
        }
        priceField.setText(String.valueOf(room.getPrice()));
        capacityField.setText(String.valueOf(room.getCapacity()));
        statusCombo.setValue(room.getStatus());
    }

    @FXML
    private void handleSave() {
        clearError();
        
        // Валидация всех полей
        ValidationUtils.ValidationResult numberResult = ValidationUtils.validateRoomNumber(numberField.getText());
        if (!numberResult.isValid()) {
            showError(numberResult.getErrorMessage());
            return;
        }
        
        String selectedType = typeCombo.getValue();
        if (selectedType == null || selectedType.trim().isEmpty()) {
            showError("Выберите тип номера");
            return;
        }
        
        ValidationUtils.ValidationResult priceResult = ValidationUtils.validatePrice(priceField.getText());
        if (!priceResult.isValid()) {
            showError(priceResult.getErrorMessage());
            return;
        }
        
        ValidationUtils.ValidationResult capacityResult = ValidationUtils.validateCapacity(capacityField.getText());
        if (!capacityResult.isValid()) {
            showError(capacityResult.getErrorMessage());
            return;
        }
        
        // Дополнительная проверка вместимости (1-5)
        try {
            int capacity = Integer.parseInt(capacityField.getText().trim());
            ValidationUtils.ValidationResult capacityRangeResult = ValidationUtils.validateCapacity(capacity);
            if (!capacityRangeResult.isValid()) {
                showAlert(Alert.AlertType.ERROR, "Ошибка валидации", capacityRangeResult.getErrorMessage());
                return;
            }
        } catch (NumberFormatException e) {
            showError("Вместимость должна быть числом");
            return;
        }
        
        try {
            Room room = selectedRoom != null ? selectedRoom : new Room();
            room.setNumber(numberField.getText().trim());
            room.setType(selectedType);
            room.setPrice(Double.parseDouble(priceField.getText().trim()));
            room.setCapacity(Integer.parseInt(capacityField.getText().trim()));
            room.setStatus(statusCombo.getValue() != null ? statusCombo.getValue() : RoomStatus.FREE);
            MainApp.getRoomService().saveRoom(room);
            refreshTable();
            clearForm();
            showSuccess("Комната успешно сохранена");
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedRoom == null) {
            showAlert(Alert.AlertType.WARNING, "Внимание", "Выберите комнату для удаления");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setHeaderText("Удаление комнаты");
        confirm.setContentText("Вы уверены, что хотите удалить комнату " + selectedRoom.getNumber() + "?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                MainApp.getRoomService().deleteRoom(selectedRoom);
                refreshTable();
                clearForm();
                showSuccess("Комната успешно удалена");
            } catch (RuntimeException e) {
                showError(e.getMessage());
            }
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
        clearError();
    }

    private void refreshTable() {
        roomsData.setAll(MainApp.getRoomService().getAllRooms());
    }

    private void clearForm() {
        selectedRoom = null;
        numberField.clear();
        typeCombo.setValue(null);
        priceField.clear();
        capacityField.clear();
        statusCombo.setValue(RoomStatus.FREE);
        roomsTable.getSelectionModel().clearSelection();
        clearError();
    }

    private void validateRoomNumber() {
        ValidationUtils.ValidationResult result = ValidationUtils.validateRoomNumber(numberField.getText());
        if (!result.isValid() && !numberField.getText().isEmpty()) {
            numberField.setStyle("-fx-border-color: #dc3545; -fx-border-width: 2px;");
        } else {
            numberField.setStyle("");
        }
    }

    private void validatePrice() {
        ValidationUtils.ValidationResult result = ValidationUtils.validatePrice(priceField.getText());
        if (!result.isValid() && !priceField.getText().isEmpty()) {
            priceField.setStyle("-fx-border-color: #dc3545; -fx-border-width: 2px;");
        } else {
            priceField.setStyle("");
        }
    }

    private void validateCapacity() {
        ValidationUtils.ValidationResult result = ValidationUtils.validateCapacity(capacityField.getText());
        if (!result.isValid() && !capacityField.getText().isEmpty()) {
            capacityField.setStyle("-fx-border-color: #dc3545; -fx-border-width: 2px;");
        } else {
            capacityField.setStyle("");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setTextFill(Color.web("#dc3545"));
        errorLabel.setVisible(true);
    }

    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setTextFill(Color.web("#28a745"));
        errorLabel.setVisible(true);
    }

    private void clearError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

