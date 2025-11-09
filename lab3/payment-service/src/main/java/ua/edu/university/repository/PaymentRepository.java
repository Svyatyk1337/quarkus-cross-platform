package ua.edu.university.repository;

import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.university.model.Payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@ApplicationScoped
public class PaymentRepository {

    private final ConcurrentHashMap<Long, Payment> payments = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public PaymentRepository() {
        // Фейкові платежі
        createPayment(new Payment(null, 1L, 2L, new BigDecimal("1500.00"),
                "UAH", Payment.PaymentStatus.COMPLETED, "Переказ другу"));
        createPayment(new Payment(null, 2L, 3L, new BigDecimal("2500.00"),
                "UAH", Payment.PaymentStatus.COMPLETED, "Оплата послуг"));
        createPayment(new Payment(null, 1L, 3L, new BigDecimal("500.00"),
                "UAH", Payment.PaymentStatus.PROCESSING, "Переказ"));
    }

    public Payment createPayment(Payment payment) {
        Long id = idGenerator.getAndIncrement();
        payment.setId(id);
        payments.put(id, payment);
        return payment;
    }

    public Optional<Payment> findById(Long id) {
        return Optional.ofNullable(payments.get(id));
    }

    public List<Payment> findAll() {
        return new ArrayList<>(payments.values());
    }

    public List<Payment> findByAccountId(Long accountId) {
        return payments.values().stream()
                .filter(payment -> payment.getFromAccountId().equals(accountId) ||
                        payment.getToAccountId().equals(accountId))
                .collect(Collectors.toList());
    }

    public Optional<Payment> updateStatus(Long id, Payment.PaymentStatus newStatus) {
        Payment payment = payments.get(id);
        if (payment == null) {
            return Optional.empty();
        }
        payment.setStatus(newStatus);
        return Optional.of(payment);
    }

    public Optional<Payment> updateTransactionId(Long id, Long transactionId) {
        Payment payment = payments.get(id);
        if (payment == null) {
            return Optional.empty();
        }
        payment.setTransactionId(transactionId);
        return Optional.of(payment);
    }

    public boolean deletePayment(Long id) {
        return payments.remove(id) != null;
    }
}
