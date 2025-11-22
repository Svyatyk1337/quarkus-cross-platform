# Лабораторна робота №4 - OIDC Security

Додавання безпеки до мікросервісної системи за допомогою OpenID Connect (OIDC) та Keycloak.

## Структура проекту

- `web-app/` - Фронтенд мікросервіс з графічним інтерфейсом (вхідна точка системи)
- `../lab3/user-service/` - Мікросервіс користувачів
- `../lab3/account-service/` - Мікросервіс облікових записів
- `../lab3/transaction-service/` - Мікросервіс транзакцій
- `../lab3/payment-service/` - Мікросервіс платежів

## Функціональність

### Web-App (Графічний інтерфейс)
- Головна сторінка з навігацією
- Перегляд користувачів
- Перегляд облікових записів
- Перегляд транзакцій
- Перегляд платежів
- Захист всіх сторінок через OIDC аутентифікацію

### Безпека
- Всі мікросервіси захищені через OIDC
- Автоматична передача токенів між сервісами
- Keycloak як Identity Provider
- Два тестових користувача:
  - `alice:alice` (роль: user)
  - `admin:admin` (ролі: admin, user)

## Запуск у DEV режимі

У dev режимі Quarkus автоматично запускає Keycloak через DevServices.

### 1. Запуск Web-App

```bash
cd web-app
./mvnw quarkus:dev
```

Web-App буде доступний на http://localhost:8080

### 2. Запуск мікросервісів з lab3

Відкрийте окремі термінали для кожного сервісу:

```bash
# Terminal 1 - User Service
cd ../lab3/user-service
./mvnw quarkus:dev -Ddebug=5006

# Terminal 2 - Account Service
cd ../lab3/account-service
./mvnw quarkus:dev -Ddebug=5007

# Terminal 3 - Transaction Service
cd ../lab3/transaction-service
./mvnw quarkus:dev -Ddebug=5008

# Terminal 4 - Payment Service
cd ../lab3/payment-service
./mvnw quarkus:dev -Ddebug=5009
```

### 3. Доступ до сервісів

- Web-App: http://localhost:8080
- Keycloak Admin Console: http://localhost:8180
  - Username: `admin`
  - Password: `admin`

### 4. Тестування

1. Відкрийте http://localhost:8080
2. Вас автоматично перенаправить на сторінку логіну Keycloak
3. Увійдіть як `alice` або `admin`
4. Перегляньте різні секції (Users, Accounts, Transactions, Payments)

## Запуск у PRODUCTION режимі

### Попередні вимоги
- Docker та Docker Compose

### 1. Збірка проектів

```bash
# Збірка мікросервісів
cd ../lab3/user-service && ./mvnw clean package
cd ../account-service && ./mvnw clean package
cd ../transaction-service && ./mvnw clean package
cd ../payment-service && ./mvnw clean package

# Збірка web-app
cd ../../lab4/web-app && ./mvnw clean package
```

### 2. Запуск через Docker Compose

```bash
cd ..
docker-compose up --build
```

### 3. Доступ до сервісів

- Web-App: http://localhost:8080
- Keycloak Admin Console: http://localhost:8180
  - Username: `admin`
  - Password: `admin`
- User Service: http://localhost:8081
- Account Service: http://localhost:8082
- Transaction Service: http://localhost:8083
- Payment Service: http://localhost:8084

### 4. Зупинка сервісів

```bash
docker-compose down
```

## Конфігурація OIDC

### Web-App
- Application Type: `web-app` (Authorization Code Flow)
- Client ID: `backend-service`
- Token Propagation: Enabled (автоматична передача токенів до backend сервісів)

### Backend Services (User, Account, Transaction, Payment)
- Application Type: `service` (Bearer Token validation)
- Client ID: `backend-service`
- Всі endpoints захищені через `@Authenticated`

## Архітектура безпеки

```
User Browser
    ↓
  [Login via Keycloak]
    ↓
  Web-App (Frontend)
    ↓ (with JWT token)
  Backend Services (User, Account, Transaction, Payment)
    ↓
  [Validate JWT with Keycloak]
```

## Технології

- Quarkus 3.29.2
- Keycloak 23.0
- OpenID Connect (OIDC)
- Qute Templates (для UI)
- REST Client with Token Propagation
- PostgreSQL (для Keycloak в production)

## Примітки

1. У dev режимі Keycloak запускається автоматично через DevServices
2. У production режимі Keycloak працює з PostgreSQL БД
3. Токени автоматично передаються між сервісами через `AccessTokenRequestReactiveFilter`
4. Всі паролі в цьому проекті призначені тільки для навчання та демонстрації

## Виконані кроки лабораторної роботи №4

### 1. Створено новий мікросервіс з графічним інтерфейсом (web-app)
- Використано Qute темплейти для відображення HTML сторінок
- Додано сторінки для перегляду користувачів, акаунтів, транзакцій та платежів
- Реалізовано інтеграцію з усіма backend мікросервісами через REST клієнти

### 2. Додано OIDC безпеку до web-app
- Налаштовано `quarkus-oidc` з application-type: `web-app` (Authorization Code Flow)
- Додано анотації `@Authenticated` до всіх ендпоінтів
- Налаштовано автоматичну передачу JWT токенів до backend сервісів

### 3. Пропаговано підтримку безпеки на всі мікросервіси
- Додано залежність `quarkus-oidc` до всіх сервісів (user, account, transaction, payment)
- Додано залежність `quarkus-rest-client-oidc-token-propagation` для міжсервісної комунікації
- Налаштовано Bearer token validation на backend сервісах
- Додано анотації `@Authenticated` до всіх Resource класів

### 4. Налаштовано Keycloak
- Створено realm конфігурацію `quarkus-realm.json` з двома користувачами:
  - alice/alice (роль: user)
  - admin/admin (ролі: admin, user)
- Налаштовано client `backend-service` для всіх сервісів
- Додано redirect URIs для dev та production режимів

### 5. Підготовлено production середовище
- Створено `docker-compose.yml` з усіма сервісами
- Налаштовано PostgreSQL для Keycloak в production
- Налаштовано networking між контейнерами
- Додано realm import для Keycloak

### 6. Конфігурація для обох режимів
- Dev режим: використовує Quarkus DevServices для автоматичного запуску Keycloak
- Production режим: використовує Docker Compose з окремими контейнерами для кожного сервісу

## Інструкції для тестування

### Тестування в DEV режимі
1. Запустіть web-app: `cd lab4/web-app && ./mvnw quarkus:dev`
2. Запустіть backend сервіси в окремих терміналах (див. секцію "Запуск у DEV режимі")
3. Відкрийте http://localhost:8080 - вас перенаправить на Keycloak login
4. Увійдіть як alice/alice або admin/admin
5. Перевірте доступ до різних секцій

### Тестування в PRODUCTION режимі
1. Зберіть всі проекти: `./mvnw clean package` в кожному сервісі
2. Запустіть: `cd lab4 && docker-compose up --build`
3. Дочекайтесь запуску всіх сервісів
4. Відкрийте http://localhost:8080
5. Увійдіть через Keycloak та перевірте функціональність

## Архітектура токенів

```
1. Користувач -> Web-App (Authorization Code Flow)
2. Web-App -> Keycloak (обмін authorization code на JWT token)
3. Web-App -> Backend Services (з JWT token в Authorization header)
4. Backend Services -> Keycloak (валідація JWT token)
5. Backend Services -> Інші Backend Services (передача JWT token через AccessTokenRequestReactiveFilter)
```
