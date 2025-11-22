package ua.edu.university.model;

import java.math.BigDecimal;

public class Account {
    public Long id;
    public Long userId;
    public String accountNumber;
    public BigDecimal balance;
    public String currency;

    public Account() {
    }

    public Account(Long id, Long userId, String accountNumber, BigDecimal balance, String currency) {
        this.id = id;
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.currency = currency;
    }
}
