package ua.edu.university.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    public Long id;
    public Long accountId;
    public String type;
    public BigDecimal amount;
    public String status;
    public LocalDateTime createdAt;

    public Transaction() {
    }

    public Transaction(Long id, Long accountId, String type, BigDecimal amount, String status, LocalDateTime createdAt) {
        this.id = id;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }
}
