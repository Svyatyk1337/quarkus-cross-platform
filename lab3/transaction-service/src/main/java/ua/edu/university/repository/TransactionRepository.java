package ua.edu.university.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.university.model.Transaction;

import java.util.List;

@ApplicationScoped
public class TransactionRepository implements PanacheRepository<Transaction> {

    public List<Transaction> findByAccountId(Long accountId) {
        return list("accountId", accountId);
    }

    public List<Transaction> findByStatus(Transaction.TransactionStatus status) {
        return list("status", status);
    }
}
