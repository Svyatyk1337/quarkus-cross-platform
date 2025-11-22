package ua.edu.university.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    public Long id;
    public Long accountId;
    public String paymentMethod;
    public BigDecimal amount;
    public String status;
    public LocalDateTime createdAt;

    public Payment() {
    }

    public Payment(Long id, Long accountId, String paymentMethod, BigDecimal amount, String status, LocalDateTime createdAt) {
        this.id = id;
        this.accountId = accountId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }
}
