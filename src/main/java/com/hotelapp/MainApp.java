package com.hotelapp;

import com.hotelapp.dao.*;
import com.hotelapp.database.Database;
import com.hotelapp.service.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * MainApp — точка входа JavaFX. Отвечает за инициализацию БД, сервисов
 * и отображение первичных окон (логин → главное меню).
 */
public class MainApp extends Application {

    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

    private static RoomService roomService;
    private static GuestService guestService;
    private static BookingService bookingService;
    private static PaymentService paymentService;
    private static AuthService authService;

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        try {
            logger.info("Запуск приложения Hotel Admin");
            this.primaryStage = stage;
            this.primaryStage.setTitle("Hotel Admin");

            logger.info("Инициализация базы данных...");
            Database.initialize();
            logger.info("База данных инициализирована");

            logger.info("Инициализация сервисов...");
            initServices();
            logger.info("Сервисы инициализированы");

            logger.info("Загрузка окна логина...");
            showLoginView();
            logger.info("Приложение успешно запущено");
        } catch (Exception e) {
            logger.error("Критическая ошибка при запуске приложения", e);
            System.err.println("Ошибка запуска приложения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initServices() {
        RoomDAO roomDAO = new RoomDAO();
        GuestDAO guestDAO = new GuestDAO();
        BookingDAO bookingDAO = new BookingDAO();
        PaymentDAO paymentDAO = new PaymentDAO();
        UserDAO userDAO = new UserDAO();

        roomService = new RoomService(roomDAO);
        guestService = new GuestService(guestDAO);
        paymentService = new PaymentService(paymentDAO);
        bookingService = new BookingService(bookingDAO, roomDAO, guestDAO, paymentDAO);
        authService = new AuthService(userDAO);
    }

    private void showLoginView() throws Exception {
        URL fxmlUrl = getClass().getResource("/ui/view/login.fxml");
        if (fxmlUrl == null) {
            throw new IllegalStateException("Не найден файл /ui/view/login.fxml");
        }
        logger.debug("Загрузка FXML: {}", fxmlUrl);

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load());
        
        URL cssUrl = getClass().getResource("/styles/main.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
            logger.debug("CSS загружен: {}", cssUrl);
        } else {
            logger.warn("CSS файл /styles/main.css не найден");
        }
        
        com.hotelapp.ui.controllers.LoginController controller = loader.getController();
        if (controller == null) {
            throw new IllegalStateException("Контроллер для login.fxml не найден");
        }
        controller.setMainApp(this);
        primaryStage.setScene(scene);
        primaryStage.show();
        logger.info("Окно логина отображено");
    }

    public void showMainView() throws Exception {
        URL fxmlUrl = getClass().getResource("/ui/view/main.fxml");
        if (fxmlUrl == null) {
            throw new IllegalStateException("Не найден файл /ui/view/main.fxml");
        }
        logger.debug("Загрузка FXML: {}", fxmlUrl);

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load());
        
        URL cssUrl = getClass().getResource("/styles/main.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
            logger.debug("CSS загружен: {}", cssUrl);
        } else {
            logger.warn("CSS файл /styles/main.css не найден");
        }
        
        com.hotelapp.ui.controllers.MainController controller = loader.getController();
        if (controller == null) {
            throw new IllegalStateException("Контроллер для main.fxml не найден");
        }
        controller.setMainApp(this);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        logger.info("Главное окно отображено");
    }

    public static RoomService getRoomService() {
        return roomService;
    }

    public static GuestService getGuestService() {
        return guestService;
    }

    public static BookingService getBookingService() {
        return bookingService;
    }

    public static PaymentService getPaymentService() {
        return paymentService;
    }

    public static AuthService getAuthService() {
        return authService;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

