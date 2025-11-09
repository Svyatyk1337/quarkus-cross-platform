#!/bin/bash

# Скрипт для запуску всіх мікросервісів

echo "========================================="
echo "Запуск мікросервісів фінансової системи"
echo "========================================="
echo ""

# Перевірка чи встановлено tmux
if ! command -v tmux &> /dev/null; then
    echo "tmux не встановлено. Встановіть його командою:"
    echo "  sudo dnf install tmux"
    echo ""
    echo "Або запустіть сервіси вручну в окремих терміналах:"
    echo "  cd user-service && ./mvnw quarkus:dev"
    echo "  cd account-service && ./mvnw quarkus:dev"
    echo "  cd transaction-service && ./mvnw quarkus:dev"
    echo "  cd payment-service && ./mvnw quarkus:dev"
    exit 1
fi

# Створення нової tmux сесії
SESSION_NAME="quarkus-microservices"

echo "Створення tmux сесії '$SESSION_NAME'..."
tmux new-session -d -s $SESSION_NAME

# User Service
echo "Запуск User Service (порт 8081)..."
tmux rename-window -t $SESSION_NAME:0 'user-service'
tmux send-keys -t $SESSION_NAME:0 "cd user-service && ./mvnw quarkus:dev" C-m

sleep 2

# Account Service
echo "Запуск Account Service (порт 8082)..."
tmux new-window -t $SESSION_NAME:1 -n 'account-service'
tmux send-keys -t $SESSION_NAME:1 "cd account-service && ./mvnw quarkus:dev" C-m

sleep 2

# Transaction Service
echo "Запуск Transaction Service (порт 8083, gRPC 9003)..."
tmux new-window -t $SESSION_NAME:2 -n 'transaction-service'
tmux send-keys -t $SESSION_NAME:2 "cd transaction-service && ./mvnw quarkus:dev" C-m

sleep 2

# Payment Service
echo "Запуск Payment Service (порт 8084)..."
tmux new-window -t $SESSION_NAME:3 -n 'payment-service'
tmux send-keys -t $SESSION_NAME:3 "cd payment-service && ./mvnw quarkus:dev" C-m

echo ""
echo "========================================="
echo "Всі сервіси запущені в tmux сесії!"
echo "========================================="
echo ""
echo "Для підключення до сесії виконайте:"
echo "  tmux attach-session -t $SESSION_NAME"
echo ""
echo "Навігація в tmux:"
echo "  Ctrl+b, а потім 0/1/2/3 - переключення між вікнами"
echo "  Ctrl+b, d - від'єднатися від сесії (сервіси продовжать працювати)"
echo ""
echo "Для зупинки всіх сервісів:"
echo "  tmux kill-session -t $SESSION_NAME"
echo ""
echo "DevUI endpoints:"
echo "  User Service:        http://localhost:8081/q/dev"
echo "  Account Service:     http://localhost:8082/q/dev"
echo "  Transaction Service: http://localhost:8083/q/dev"
echo "  Payment Service:     http://localhost:8084/q/dev"
echo ""
