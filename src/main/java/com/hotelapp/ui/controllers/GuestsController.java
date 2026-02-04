package com.hotelapp.ui.controllers;

import com.hotelapp.MainApp;
import com.hotelapp.model.Guest;
import com.hotelapp.util.ValidationUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

/**
 * GuestsController — CRUD гостей с валидацией.
 */
public class GuestsController {

    @FXML
    private TableView<Guest> guestsTable;
    @FXML
    private TableColumn<Guest, String> nameColumn;
    @FXML
    private TableColumn<Guest, String> passportColumn;
    @FXML
    private TableColumn<Guest, String> phoneColumn;

    @FXML
    private TextField nameField;
    @FXML
    private TextField passportField;
    @FXML
    private TextField phoneField;
    @FXML
    private Label errorLabel;

    private final ObservableList<Guest> guestsData = FXCollections.observableArrayList();
    private Guest selectedGuest;

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        passportColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPassport()));
        phoneColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));

        guestsTable.setItems(guestsData);
        guestsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedGuest = newVal;
            populateForm(newVal);
            clearError();
        });
        refreshGuests();
        
        // Валидация в реальном времени
        nameField.textProperty().addListener((obs, oldV, newV) -> validateName());
        passportField.textProperty().addListener((obs, oldV, newV) -> validatePassport());
        phoneField.textProperty().addListener((obs, oldV, newV) -> validatePhone());
    }

    @FXML
    private void handleSave() {
        clearError();
        
        // Валидация всех полей
        ValidationUtils.ValidationResult nameResult = ValidationUtils.validateName(nameField.getText());
        if (!nameResult.isValid()) {
            showError(nameResult.getErrorMessage());
            return;
        }
        
        ValidationUtils.ValidationResult passportResult = ValidationUtils.validatePassport(passportField.getText());
        if (!passportResult.isValid()) {
            showError(passportResult.getErrorMessage());
            return;
        }
        
        ValidationUtils.ValidationResult phoneResult = ValidationUtils.validatePhone(phoneField.getText());
        if (!phoneResult.isValid()) {
            showError(phoneResult.getErrorMessage());
            return;
        }
        
        try {
            Guest guest = selectedGuest != null ? selectedGuest : new Guest();
            guest.setName(nameField.getText().trim());
            guest.setPassport(passportField.getText().trim().toUpperCase());
            guest.setPhone(phoneField.getText().trim());
            MainApp.getGuestService().saveGuest(guest);
            refreshGuests();
            clearForm();
            showSuccess("Гость успешно сохранён");
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedGuest == null) {
            showAlert(Alert.AlertType.WARNING, "Внимание", "Выберите гостя для удаления");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setHeaderText("Удаление гостя");
        confirm.setContentText("Вы уверены, что хотите удалить гостя " + selectedGuest.getName() + "?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                MainApp.getGuestService().deleteGuest(selectedGuest);
                refreshGuests();
                clearForm();
                showSuccess("Гость успешно удалён");
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

    private void refreshGuests() {
        guestsData.setAll(MainApp.getGuestService().getAllGuests());
    }

    private void populateForm(Guest guest) {
        if (guest == null) {
            clearForm();
            return;
        }
        nameField.setText(guest.getName());
        passportField.setText(guest.getPassport());
        phoneField.setText(guest.getPhone());
    }

    private void clearForm() {
        selectedGuest = null;
        nameField.clear();
        passportField.clear();
        phoneField.clear();
        guestsTable.getSelectionModel().clearSelection();
        clearError();
    }

    private void validateName() {
        ValidationUtils.ValidationResult result = ValidationUtils.validateName(nameField.getText());
        if (!result.isValid() && !nameField.getText().isEmpty()) {
            nameField.setStyle("-fx-border-color: #dc3545; -fx-border-width: 2px;");
        } else {
            nameField.setStyle("");
        }
    }

    private void validatePassport() {
        ValidationUtils.ValidationResult result = ValidationUtils.validatePassport(passportField.getText());
        if (!result.isValid() && !passportField.getText().isEmpty()) {
            passportField.setStyle("-fx-border-color: #dc3545; -fx-border-width: 2px;");
        } else {
            passportField.setStyle("");
        }
    }

    private void validatePhone() {
        ValidationUtils.ValidationResult result = ValidationUtils.validatePhone(phoneField.getText());
        if (!result.isValid() && !phoneField.getText().isEmpty()) {
            phoneField.setStyle("-fx-border-color: #dc3545; -fx-border-width: 2px;");
        } else {
            phoneField.setStyle("");
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

