package ua.edu.university.repository;

import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.university.model.Transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@ApplicationScoped
public class TransactionRepository {

    private final ConcurrentHashMap<Long, Transaction> transactions = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public TransactionRepository() {
        // Фейкові транзакції для рахунків 1, 2, 3
        createTransaction(new Transaction(null, 1L, Transaction.TransactionType.DEPOSIT,
                new BigDecimal("5000.00"), "UAH", Transaction.TransactionStatus.COMPLETED,
                "Поповнення рахунку"));
        createTransaction(new Transaction(null, 1L, Transaction.TransactionType.WITHDRAWAL,
                new BigDecimal("1000.00"), "UAH", Transaction.TransactionStatus.COMPLETED,
                "Зняття готівки"));
        createTransaction(new Transaction(null, 2L, Transaction.TransactionType.PAYMENT,
                new BigDecimal("500.50"), "UAH", Transaction.TransactionStatus.COMPLETED,
                "Оплата послуг"));
        createTransaction(new Transaction(null, 3L, Transaction.TransactionType.TRANSFER,
                new BigDecimal("10000.00"), "UAH", Transaction.TransactionStatus.PENDING,
                "Переказ коштів"));
    }

    public Transaction createTransaction(Transaction transaction) {
        Long id = idGenerator.getAndIncrement();
        transaction.setId(id);
        transactions.put(id, transaction);
        return transaction;
    }

    public Optional<Transaction> findById(Long id) {
        return Optional.ofNullable(transactions.get(id));
    }

    public List<Transaction> findAll() {
        return new ArrayList<>(transactions.values());
    }

    public List<Transaction> findByAccountId(Long accountId) {
        return transactions.values().stream()
                .filter(transaction -> transaction.getAccountId().equals(accountId))
                .collect(Collectors.toList());
    }

    public Optional<Transaction> updateStatus(Long id, Transaction.TransactionStatus newStatus) {
        Transaction transaction = transactions.get(id);
        if (transaction == null) {
            return Optional.empty();
        }
        transaction.setStatus(newStatus);
        return Optional.of(transaction);
    }

    public boolean deleteTransaction(Long id) {
        return transactions.remove(id) != null;
    }
}
