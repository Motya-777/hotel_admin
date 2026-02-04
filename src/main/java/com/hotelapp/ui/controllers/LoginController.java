package com.hotelapp.ui.controllers;

import com.hotelapp.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

/**
 * LoginController — авторизация администратора.
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleLogin() {
        clearError();
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            showError("Введите логин и пароль");
            return;
        }
        boolean success = MainApp.getAuthService().login(username, password);
        if (success) {
            try {
                mainApp.showMainView();
            } catch (Exception e) {
                showError("Не удалось открыть главное окно: " + e.getMessage());
            }
        } else {
            showError("Неверный логин или пароль");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setTextFill(Color.web("#dc3545"));
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void clearError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }
}

