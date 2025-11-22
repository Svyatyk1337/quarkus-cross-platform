package ua.edu.university.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.university.model.Payment;

import java.util.List;

@ApplicationScoped
public class PaymentRepository implements PanacheRepository<Payment> {

    public List<Payment> findByAccountId(Long accountId) {
        return list("fromAccountId = ?1 or toAccountId = ?1", accountId);
    }

    public List<Payment> findByStatus(Payment.PaymentStatus status) {
        return list("status", status);
    }

    public List<Payment> findByFromAccountId(Long fromAccountId) {
        return list("fromAccountId", fromAccountId);
    }

    public List<Payment> findByToAccountId(Long toAccountId) {
        return list("toAccountId", toAccountId);
    }
}
