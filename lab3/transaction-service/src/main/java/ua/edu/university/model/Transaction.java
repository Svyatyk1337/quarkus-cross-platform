package ua.edu.university.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private Long id;
    private Long accountId;
    private TransactionType type;
    private BigDecimal amount;
    private String currency;
    private TransactionStatus status;
    private String description;
    private LocalDateTime createdAt;

    public enum TransactionType {
        DEPOSIT,      // Поповнення
        WITHDRAWAL,   // Зняття
        TRANSFER,     // Переказ
        PAYMENT       // Платіж
    }

    public enum TransactionStatus {
        PENDING,      // Очікує обробки
        COMPLETED,    // Завершено
        FAILED,       // Не вдалося
        CANCELLED     // Скасовано
    }

    public Transaction() {
        this.createdAt = LocalDateTime.now();
        this.status = TransactionStatus.PENDING;
        this.currency = "UAH";
    }

    public Transaction(Long id, Long accountId, TransactionType type, BigDecimal amount,
                       String currency, TransactionStatus status, String description) {
        this.id = id;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
