# hotel-admin

Учебный JavaFX‑проект для администрирования гостиницы. Архитектура разделена на слои MVC: UI (FXML + контроллеры) → Service → DAO → Database (SQLite).

## Требования

- Java 17
- Maven 3.8+
- Доступ к сети для загрузки зависимостей Maven

## Сборка и запуск

```bash
mvn clean package
mvn javafx:run
```

Приложение стартует с окна входа (admin / admin123). SQLite-файл `hotel.db` создаётся в корне проекта. SQL‑схема хранится в `src/main/resources/sql/schema.sql` и выполняется автоматически при первом запуске.

## Структура

- `com.hotelapp.MainApp` — запуск JavaFX, инициализация Database/Service.
- `com.hotelapp.database` — подключение к SQLite, запуск миграций, тестовые данные.
- `com.hotelapp.model` — модели Room, Guest, Booking, Payment, User + enum статусов.
- `com.hotelapp.dao` — CRUD для каждой сущности.
- `com.hotelapp.service` — бизнес-логика бронирований, оплат, авторизации.
- `com.hotelapp.ui.controllers` — контроллеры JavaFX, связывают UI и сервисы.
- `src/main/resources/ui/view` — FXML-файлы (login/main/rooms/...).
- `src/main/resources/ui/css` — базовые стили.
- `src/main/resources/sql/schema.sql` — создание таблиц.

## Демо-данные

При первом запуске автоматически добавляются:

- Пользователь admin/admin123.
- 3 номера (Single/Double/Suite).
- 2 гостя.
- 1 бронь на ближайшие даты.

## Функциональность

- Авторизация администратора.
- CRUD номеров и гостей.
- Бронирования с проверкой пересечений дат и расчётом стоимости.
- Заселение и выселение (изменение статусов, учёт оплат).
- Отчётные таблицы по текущим бронированиям и состоянию номеров.

## Тесты

Для примера добавлен тест DAO (см. `src/test/java/...`) — запуск `mvn test`.

