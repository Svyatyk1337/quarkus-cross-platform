# Лабораторна робота №3 - Мікросервісна архітектура на Quarkus

## Доменна модель: Фінанси

### Архітектура мікросервісів

Система складається з 4 мікросервісів:

#### 1. User Service (порт 8081)
- **Відповідальність**: Управління користувачами системи
- **Функціонал**:
  - Створення користувачів
  - Отримання інформації про користувачів
  - Оновлення профілів користувачів
- **Endpoints**: REST API

#### 2. Account Service (порт 8082)
- **Відповідальність**: Управління фінансовими рахунками
- **Функціонал**:
  - Створення рахунків (поточні, накопичувальні)
  - Отримання інформації про рахунки
  - Оновлення балансу рахунків
- **Залежності**: Викликає User Service через REST для перевірки користувачів
- **Endpoints**: REST API

#### 3. Transaction Service (порт 8083)
- **Відповідальність**: Обробка та збереження транзакцій
- **Функціонал**:
  - Створення транзакцій
  - Отримання історії транзакцій
  - Перевірка статусу транзакцій
- **Залежності**: Викликає Account Service через REST для перевірки рахунків
- **Endpoints**: REST API та gRPC

#### 4. Payment Service (порт 8084)
- **Відповідальність**: Обробка платежів
- **Функціонал**:
  - Ініціювання платежів
  - Обробка переказів між рахунками
  - Перевірка статусу платежів
- **Залежності**:
  - Викликає Account Service через REST
  - Викликає Transaction Service через gRPC
- **Endpoints**: REST API

### Діаграма взаємодії

```
User Service (8081)
    ↑
    | REST
    |
Account Service (8082)
    ↑
    | REST
    |
Transaction Service (8083)
    ↑
    | gRPC
    |
Payment Service (8084)
```

### Технології
- **Framework**: Quarkus
- **Комунікація**: REST (JAX-RS), gRPC
- **Дані**: In-memory фейкові репозиторії
- **Тестування**: Quarkus DevUI

### Запуск

Кожен сервіс запускається окремо:

```bash
cd user-service
./mvnw quarkus:dev

cd account-service
./mvnw quarkus:dev

cd transaction-service
./mvnw quarkus:dev

cd payment-service
./mvnw quarkus:dev
```

### Тестування через DevUI

Після запуску сервісів, DevUI доступний за адресами:
- User Service: http://localhost:8081/q/dev
- Account Service: http://localhost:8082/q/dev
- Transaction Service: http://localhost:8083/q/dev
- Payment Service: http://localhost:8084/q/dev
