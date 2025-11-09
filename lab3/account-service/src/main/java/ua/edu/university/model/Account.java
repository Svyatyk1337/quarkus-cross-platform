package ua.edu.university.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Account {
    private Long id;
    private Long userId;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private String currency;
    private LocalDateTime createdAt;

    public enum AccountType {
        CHECKING,    // Поточний рахунок
        SAVINGS,     // Накопичувальний рахунок
        INVESTMENT   // Інвестиційний рахунок
    }

    public Account() {
        this.createdAt = LocalDateTime.now();
        this.balance = BigDecimal.ZERO;
        this.currency = "UAH";
    }

    public Account(Long id, Long userId, String accountNumber, AccountType accountType, BigDecimal balance, String currency) {
        this.id = id;
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.currency = currency;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
