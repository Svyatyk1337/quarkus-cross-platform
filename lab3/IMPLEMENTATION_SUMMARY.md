# Підсумок виконання лабораторної роботи №3

## Виконані завдання

### 1. ✅ Обрано доменну модель проєкту
**Доменна модель:** Фінанси

Система управління фінансами включає:
- Управління користувачами
- Управління банківськими рахунками
- Обробка фінансових транзакцій
- Обробка платежів між рахунками

### 2. ✅ Розплановано мікросервісну архітектуру

Система складається з **4 мікросервісів**:

#### User Service (порт 8081)
- **Відповідальність**: Управління користувачами системи
- **Технології**: Quarkus REST
- **Функціонал**:
  - CRUD операції над користувачами
  - Перевірка існування користувачів
- **Фейкові дані**: 3 користувачі

#### Account Service (порт 8082)
- **Відповідальність**: Управління фінансовими рахунками
- **Технології**: Quarkus REST, REST Client
- **Залежності**: User Service (REST)
- **Функціонал**:
  - CRUD операції над рахунками
  - Управління балансом
  - Перевірка існування рахунків
- **Фейкові дані**: 4 рахунки різних типів (CHECKING, SAVINGS, INVESTMENT)

#### Transaction Service (порт 8083, gRPC 9003)
- **Відповідальність**: Обробка та збереження транзакцій
- **Технології**: Quarkus REST, REST Client, gRPC Server
- **Залежності**: Account Service (REST)
- **Функціонал**:
  - CRUD операції над транзакціями
  - gRPC API для створення транзакцій
  - Управління статусами транзакцій
- **Фейкові дані**: 4 транзакції різних типів (DEPOSIT, WITHDRAWAL, PAYMENT, TRANSFER)

#### Payment Service (порт 8084)
- **Відповідальність**: Обробка платежів
- **Технології**: Quarkus REST, REST Client, gRPC Client
- **Залежності**:
  - Account Service (REST) - перевірка рахунків
  - Transaction Service (gRPC) - створення транзакцій
- **Функціонал**:
  - Створення платежів між рахунками
  - Автоматичне створення транзакцій через gRPC
  - Управління статусами платежів
- **Фейкові дані**: 3 платежі різних статусів

### 3. ✅ Створено фейкові репозиторії даних

Кожен сервіс містить:
- In-memory репозиторій на основі `ConcurrentHashMap`
- Попередньо завантажені тестові дані
- Повний набір CRUD операцій

**Структури даних:**
- `User`: id, firstName, lastName, email, phone, createdAt
- `Account`: id, userId, accountNumber, accountType, balance, currency, createdAt
- `Transaction`: id, accountId, type, amount, currency, status, description, createdAt
- `Payment`: id, fromAccountId, toAccountId, amount, currency, status, description, transactionId, createdAt

### 4. ✅ Налаштовано синхронну комунікацію

#### REST комунікація (Розділи 4.1-4.3 з Quarkus in Action)

**Account Service → User Service**
- Використовує `@RegisterRestClient` та `@RestClient`
- При створенні рахунку перевіряє чи існує користувач
- Файли:
  - `account-service/src/main/java/ua/edu/university/client/UserServiceClient.java`
  - `account-service/src/main/resources/application.properties` (конфігурація)

**Transaction Service → Account Service**
- Використовує `@RegisterRestClient` та `@RestClient`
- При створенні транзакції перевіряє чи існує рахунок
- Файли:
  - `transaction-service/src/main/java/ua/edu/university/client/AccountServiceClient.java`
  - `transaction-service/src/main/resources/application.properties` (конфігурація)

**Payment Service → Account Service**
- Використовує `@RegisterRestClient` та `@RestClient`
- При створенні платежу перевіряє обидва рахунки (джерело та призначення)
- Файли:
  - `payment-service/src/main/java/ua/edu/university/client/AccountServiceClient.java`
  - `payment-service/src/main/resources/application.properties` (конфігурація)

#### gRPC комунікація (Розділи 4.6-4.8 з Quarkus in Action)

**Transaction Service - gRPC Server**
- Визначено Protocol Buffers схему в `transaction.proto`
- Імплементовано gRPC сервіс `TransactionGrpcService`
- Порт gRPC: 9003
- Файли:
  - `transaction-service/src/main/proto/transaction.proto`
  - `transaction-service/src/main/java/ua/edu/university/grpc/TransactionGrpcService.java`

**Payment Service → Transaction Service (gRPC)**
- Використовує `@GrpcClient` для підключення
- При створенні платежу автоматично створює транзакцію через gRPC
- Демонструє реактивне програмування з Mutiny (`.await().indefinitely()`)
- Файли:
  - `payment-service/src/main/proto/transaction.proto`
  - `payment-service/src/main/java/ua/edu/university/resource/PaymentResource.java`

### 5. ✅ Можливість тестування через DevUI

**DevUI доступний для всіх сервісів:**
- User Service: http://localhost:8081/q/dev
- Account Service: http://localhost:8082/q/dev
- Transaction Service: http://localhost:8083/q/dev
- Payment Service: http://localhost:8084/q/dev

**Що можна тестувати через DevUI:**
- REST endpoints кожного сервісу
- Налаштовані REST Clients
- gRPC сервіси та клієнти
- Конфігурації сервісів
- Логи та метрики

**Додаткові інструменти для тестування:**
- `API_TESTING.md` - детальні приклади curl команд
- `start-all.sh` - скрипт для одночасного запуску всіх сервісів

## Архітектурна діаграма

```
┌─────────────────┐
│  User Service   │ :8081
│    (REST API)   │
└────────┬────────┘
         │
         │ REST
         │
┌────────▼────────┐
│ Account Service │ :8082
│   (REST API +   │
│   REST Client)  │
└────────┬────────┘
         │
         │ REST
         │
┌────────▼────────┐
│Transaction Svc  │ :8083 (HTTP) / :9003 (gRPC)
│   (REST API +   │
│   gRPC Server)  │
└────────┬────────┘
         │
         │ gRPC
         │
┌────────▼────────┐
│ Payment Service │ :8084
│   (REST API +   │
│   REST Client + │
│   gRPC Client)  │
└─────────────────┘
```

## Демонстрація комунікації

### REST комунікація
1. **Account Service → User Service**: Перевірка користувача при створенні рахунку
2. **Transaction Service → Account Service**: Перевірка рахунку при створенні транзакції
3. **Payment Service → Account Service**: Перевірка обох рахунків при створенні платежу

### gRPC комунікація
1. **Payment Service → Transaction Service**: Створення транзакції при обробці платежу

### Комбінована взаємодія (найскладніший сценарій)
При створенні платежу (`POST /api/payments`):
1. Payment Service → Account Service (REST) - перевірка рахунку-джерела
2. Payment Service → Account Service (REST) - перевірка рахунку-призначення
3. Payment Service → Transaction Service (gRPC) - створення транзакції
4. Transaction Service → Account Service (REST) - перевірка рахунку для транзакції

## Використані технології

- **Framework**: Quarkus 3.6.4
- **Java Version**: Java 17+
- **REST**: JAX-RS (RESTEasy Reactive)
- **REST Client**: MicroProfile REST Client
- **gRPC**: Quarkus gRPC (на базі Vert.x)
- **Reactive**: SmallRye Mutiny
- **Serialization**: Jackson (JSON)
- **Build Tool**: Maven

## Запуск та тестування

### Запуск всіх сервісів
```bash
./start-all.sh
```

### Запуск окремого сервісу
```bash
cd user-service
./mvnw quarkus:dev
```

### Приклад тестування взаємодії
Див. файл `API_TESTING.md` для детальних прикладів

## Висновки

Реалізовано повнофункціональну мікросервісну архітектуру з:
- ✅ 4 мікросервіси з чітким розділенням відповідальності
- ✅ REST комунікація між сервісами (3 приклади)
- ✅ gRPC комунікація (1 приклад)
- ✅ Фейкові репозиторії з реалістичними даними
- ✅ Можливість тестування через DevUI та curl
- ✅ Обробка помилок при недоступності залежних сервісів
- ✅ Повна документація та приклади використання

Проєкт демонструє основні принципи мікросервісної архітектури та різні способи синхронної комунікації між сервісами на платформі Quarkus.
