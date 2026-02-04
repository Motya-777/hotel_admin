package com.hotelapp.ui.controllers;

import com.hotelapp.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

/**
 * MainController — главное меню и контейнер для вкладок.
 */
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @FXML
    private BorderPane mainPane;

    @FXML
    private StackPane contentHolder;

    public void setMainApp(MainApp mainApp) {
        loadView("/ui/view/rooms.fxml");
    }

    @FXML
    private void openRooms() {
        loadView("/ui/view/rooms.fxml");
    }

    @FXML
    private void openGuests() {
        loadView("/ui/view/guests.fxml");
    }

    @FXML
    private void openBookings() {
        loadView("/ui/view/bookings.fxml");
    }

    @FXML
    private void openCheckIn() {
        loadView("/ui/view/checkin.fxml");
    }

    @FXML
    private void openCheckOut() {
        loadView("/ui/view/checkout.fxml");
    }

    @FXML
    private void openReports() {
        loadView("/ui/view/reports.fxml");
    }

    private void loadView(String resource) {
        try {
            URL resourceUrl = getClass().getResource(resource);
            if (resourceUrl == null) {
                logger.error("Ресурс не найден: {}", resource);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Файл не найден");
                alert.setContentText("Не удалось найти файл: " + resource);
                alert.showAndWait();
                return;
            }
            
            logger.debug("Загрузка FXML: {}", resourceUrl);
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Node content = loader.load();
            contentHolder.getChildren().clear();
            contentHolder.getChildren().add(content);
            logger.debug("FXML успешно загружен: {}", resource);
        } catch (IOException e) {
            logger.error("Ошибка загрузки FXML: {}", resource, e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Не удалось загрузить вид");
            alert.setContentText("Ошибка при загрузке " + resource + ":\n" + e.getMessage());
            alert.showAndWait();
        }
    }
}

