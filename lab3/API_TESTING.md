# Тестування API мікросервісів

## Послідовність запуску сервісів

Запускайте кожен сервіс в окремому терміналі:

```bash
# Термінал 1 - User Service
cd user-service
./mvnw quarkus:dev

# Термінал 2 - Account Service (залежить від User Service)
cd account-service
./mvnw quarkus:dev

# Термінал 3 - Transaction Service (залежить від Account Service)
cd transaction-service
./mvnw quarkus:dev

# Термінал 4 - Payment Service (залежить від Account та Transaction Services)
cd payment-service
./mvnw quarkus:dev
```

## DevUI

Після запуску всіх сервісів, DevUI доступний за адресами:
- User Service: http://localhost:8081/q/dev
- Account Service: http://localhost:8082/q/dev
- Transaction Service: http://localhost:8083/q/dev
- Payment Service: http://localhost:8084/q/dev

## 1. User Service API (http://localhost:8081)

### Отримати всіх користувачів
```bash
curl http://localhost:8081/api/users
```

### Отримати користувача за ID
```bash
curl http://localhost:8081/api/users/1
```

### Створити нового користувача
```bash
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Тарас",
    "lastName": "Іванов",
    "email": "taras.ivanov@example.com",
    "phone": "+380504567890"
  }'
```

### Перевірити чи існує користувач
```bash
curl http://localhost:8081/api/users/1/exists
```

## 2. Account Service API (http://localhost:8082)

**Демонструє REST комунікацію з User Service**

### Отримати всі рахунки
```bash
curl http://localhost:8082/api/accounts
```

### Отримати рахунок за ID
```bash
curl http://localhost:8082/api/accounts/1
```

### Отримати рахунки користувача
```bash
curl http://localhost:8082/api/accounts/user/1
```

### Створити новий рахунок (з перевіркою користувача через REST)
```bash
# Спробуємо створити для існуючого користувача (user_id=1)
curl -X POST http://localhost:8082/api/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "accountType": "SAVINGS",
    "balance": 10000,
    "currency": "UAH"
  }'

# Спробуємо створити для неіснуючого користувача (має повернути помилку)
curl -X POST http://localhost:8082/api/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 999,
    "accountType": "CHECKING",
    "balance": 5000,
    "currency": "UAH"
  }'
```

### Оновити баланс рахунку
```bash
curl -X PUT http://localhost:8082/api/accounts/1/balance \
  -H "Content-Type: application/json" \
  -d '{
    "newBalance": 25000.50
  }'
```

## 3. Transaction Service API (http://localhost:8083)

**Демонструє REST комунікацію з Account Service**

### Отримати всі транзакції
```bash
curl http://localhost:8083/api/transactions
```

### Отримати транзакцію за ID
```bash
curl http://localhost:8083/api/transactions/1
```

### Отримати транзакції рахунку
```bash
curl http://localhost:8083/api/transactions/account/1
```

### Створити нову транзакцію (з перевіркою рахунку через REST)
```bash
# Створення транзакції для існуючого рахунку
curl -X POST http://localhost:8083/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": 1,
    "type": "DEPOSIT",
    "amount": 3000,
    "currency": "UAH",
    "description": "Зарплата"
  }'

# Спроба створення для неіснуючого рахунку (має повернути помилку)
curl -X POST http://localhost:8083/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": 999,
    "type": "WITHDRAWAL",
    "amount": 1000,
    "currency": "UAH",
    "description": "Тест"
  }'
```

### Оновити статус транзакції
```bash
curl -X PUT http://localhost:8083/api/transactions/1/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "COMPLETED"
  }'
```

## 4. Payment Service API (http://localhost:8084)

**Демонструє REST комунікацію з Account Service та gRPC комунікацію з Transaction Service**

### Отримати всі платежі
```bash
curl http://localhost:8084/api/payments
```

### Отримати платіж за ID
```bash
curl http://localhost:8084/api/payments/1
```

### Отримати платежі рахунку
```bash
curl http://localhost:8084/api/payments/account/1
```

### Створити новий платіж (демонстрація REST + gRPC)
```bash
# Цей запит:
# 1. Перевіряє рахунки через REST (Account Service)
# 2. Створює транзакцію через gRPC (Transaction Service)
curl -X POST http://localhost:8084/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountId": 1,
    "toAccountId": 2,
    "amount": 750.00,
    "currency": "UAH",
    "description": "Переказ коштів між рахунками"
  }'

# Спроба створення з неіснуючим рахунком (має повернути помилку від Account Service)
curl -X POST http://localhost:8084/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountId": 999,
    "toAccountId": 2,
    "amount": 100,
    "currency": "UAH",
    "description": "Тест"
  }'
```

## Тестування взаємодії сервісів

### Сценарій 1: Повний цикл створення платежу

```bash
# 1. Створити користувача
USER_RESPONSE=$(curl -s -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Петро",
    "lastName": "Сидоренко",
    "email": "petro@example.com",
    "phone": "+380501111111"
  }')
echo "User created: $USER_RESPONSE"

# 2. Створити рахунок для користувача (перевірка через REST)
ACCOUNT_RESPONSE=$(curl -s -X POST http://localhost:8082/api/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "accountType": "CHECKING",
    "balance": 50000,
    "currency": "UAH"
  }')
echo "Account created: $ACCOUNT_RESPONSE"

# 3. Створити платіж (REST до Account + gRPC до Transaction)
PAYMENT_RESPONSE=$(curl -s -X POST http://localhost:8084/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountId": 1,
    "toAccountId": 2,
    "amount": 1500,
    "currency": "UAH",
    "description": "Тестовий переказ"
  }')
echo "Payment created: $PAYMENT_RESPONSE"

# 4. Перевірити створену транзакцію
curl http://localhost:8083/api/transactions/account/1
```

### Сценарій 2: Перевірка обробки помилок

```bash
# Спроба створити рахунок для неіснуючого користувача
curl -X POST http://localhost:8082/api/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 9999,
    "accountType": "CHECKING",
    "balance": 1000,
    "currency": "UAH"
  }'
# Очікується: 400 Bad Request - "User does not exist"

# Спроба створити транзакцію для неіснуючого рахунку
curl -X POST http://localhost:8083/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": 9999,
    "type": "DEPOSIT",
    "amount": 1000,
    "currency": "UAH",
    "description": "Test"
  }'
# Очікується: 400 Bad Request - "Account does not exist"

# Спроба створити платіж з неіснуючими рахунками
curl -X POST http://localhost:8084/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountId": 9999,
    "toAccountId": 8888,
    "amount": 500,
    "currency": "UAH",
    "description": "Test"
  }'
# Очікується: 400 Bad Request - "Source account does not exist"
```

## Перевірка комунікації

### REST комунікація:
- **Account Service → User Service**: При створенні рахунку перевіряється існування користувача
- **Transaction Service → Account Service**: При створенні транзакції перевіряється існування рахунку
- **Payment Service → Account Service**: При створенні платежу перевіряються обидва рахунки

### gRPC комунікація:
- **Payment Service → Transaction Service**: При створенні платежу автоматично створюється транзакція через gRPC

## Моніторинг через DevUI

1. Відкрийте DevUI кожного сервісу
2. Перейдіть в розділ "REST Clients" для перегляду налаштованих REST клієнтів
3. Перейдіть в розділ "gRPC" для перегляду gRPC сервісів та клієнтів
4. Використовуйте вбудовані інструменти для тестування endpoints
