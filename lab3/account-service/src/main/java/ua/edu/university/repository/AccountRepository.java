package ua.edu.university.repository;

import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.university.model.Account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@ApplicationScoped
public class AccountRepository {

    private final ConcurrentHashMap<Long, Account> accounts = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final Random random = new Random();

    public AccountRepository() {
        // Фейкові рахунки для користувачів 1, 2, 3
        createAccount(new Account(null, 1L, generateAccountNumber(),
            Account.AccountType.CHECKING, new BigDecimal("15000.50"), "UAH"));
        createAccount(new Account(null, 1L, generateAccountNumber(),
            Account.AccountType.SAVINGS, new BigDecimal("50000.00"), "UAH"));
        createAccount(new Account(null, 2L, generateAccountNumber(),
            Account.AccountType.CHECKING, new BigDecimal("8500.75"), "UAH"));
        createAccount(new Account(null, 3L, generateAccountNumber(),
            Account.AccountType.INVESTMENT, new BigDecimal("120000.00"), "UAH"));
    }

    private String generateAccountNumber() {
        return "UA" + String.format("%014d", random.nextInt(1000000000));
    }

    public Account createAccount(Account account) {
        Long id = idGenerator.getAndIncrement();
        account.setId(id);
        if (account.getAccountNumber() == null || account.getAccountNumber().isEmpty()) {
            account.setAccountNumber(generateAccountNumber());
        }
        accounts.put(id, account);
        return account;
    }

    public Optional<Account> findById(Long id) {
        return Optional.ofNullable(accounts.get(id));
    }

    public List<Account> findAll() {
        return new ArrayList<>(accounts.values());
    }

    public List<Account> findByUserId(Long userId) {
        return accounts.values().stream()
                .filter(account -> account.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public Optional<Account> updateBalance(Long id, BigDecimal newBalance) {
        Account account = accounts.get(id);
        if (account == null) {
            return Optional.empty();
        }
        account.setBalance(newBalance);
        return Optional.of(account);
    }

    public boolean existsById(Long id) {
        return accounts.containsKey(id);
    }

    public boolean deleteAccount(Long id) {
        return accounts.remove(id) != null;
    }
}
