package ua.edu.university.grpc;

import io.grpc.Status;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import ua.edu.university.client.AccountServiceClient;
import ua.edu.university.model.Transaction;
import ua.edu.university.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@GrpcService
public class TransactionGrpcService implements TransactionService {

    @Inject
    TransactionRepository transactionRepository;

    @Inject
    @RestClient
    AccountServiceClient accountServiceClient;

    @Override
    @jakarta.transaction.Transactional
    public Uni<TransactionResponse> createTransaction(CreateTransactionRequest request) {
        return Uni.createFrom().item(() -> {
            try {
                AccountServiceClient.ExistsResponse response =
                        accountServiceClient.checkAccountExists(request.getAccountId());
                if (!response.exists) {
                    throw Status.INVALID_ARGUMENT
                            .withDescription("Account with id " + request.getAccountId() + " does not exist")
                            .asRuntimeException();
                }
            } catch (Exception e) {
                throw Status.UNAVAILABLE
                        .withDescription("Account service is unavailable")
                        .asRuntimeException();
            }

            Transaction transaction = new Transaction();
            transaction.setAccountId(request.getAccountId());
            transaction.setType(Transaction.TransactionType.valueOf(request.getType()));
            transaction.setAmount(new BigDecimal(request.getAmount()));
            transaction.setCurrency(request.getCurrency());
            transaction.setDescription(request.getDescription());

            transactionRepository.persist(transaction);
            return mapToResponse(transaction);
        });
    }

    @Override
    public Uni<TransactionResponse> getTransaction(GetTransactionRequest request) {
        return Uni.createFrom().item(() -> {
            Transaction transaction = transactionRepository.findById(request.getId());
            if (transaction == null) {
                throw Status.NOT_FOUND
                        .withDescription("Transaction not found")
                        .asRuntimeException();
            }
            return mapToResponse(transaction);
        });
    }

    @Override
    public Uni<TransactionListResponse> getAccountTransactions(GetAccountTransactionsRequest request) {
        return Uni.createFrom().item(() -> {
            List<Transaction> transactions = transactionRepository.findByAccountId(request.getAccountId());
            List<TransactionResponse> responses = transactions.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            return TransactionListResponse.newBuilder()
                    .addAllTransactions(responses)
                    .build();
        });
    }

    @Override
    @jakarta.transaction.Transactional
    public Uni<TransactionResponse> updateTransactionStatus(UpdateStatusRequest request) {
        return Uni.createFrom().item(() -> {
            Transaction transaction = transactionRepository.findById(request.getId());
            if (transaction == null) {
                throw Status.NOT_FOUND
                        .withDescription("Transaction not found")
                        .asRuntimeException();
            }
            transaction.setStatus(Transaction.TransactionStatus.valueOf(request.getStatus()));
            return mapToResponse(transaction);
        });
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.newBuilder()
                .setId(transaction.getId())
                .setAccountId(transaction.getAccountId())
                .setType(transaction.getType().name())
                .setAmount(transaction.getAmount().toString())
                .setCurrency(transaction.getCurrency())
                .setStatus(transaction.getStatus().name())
                .setDescription(transaction.getDescription())
                .setCreatedAt(transaction.getCreatedAt().toString())
                .build();
    }
}
