package ua.edu.university.resource;

import io.quarkus.grpc.GrpcClient;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import ua.edu.university.client.AccountServiceClient;
import ua.edu.university.grpc.*;
import ua.edu.university.model.Payment;
import ua.edu.university.repository.PaymentRepository;

import java.util.List;

@Path("/api/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class PaymentResource {

    @Inject
    PaymentRepository paymentRepository;

    @Inject
    @RestClient
    AccountServiceClient accountServiceClient;

    @GrpcClient("transaction-service")
    TransactionService transactionService;

    @GET
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getPaymentById(@PathParam("id") Long id) {
        return paymentRepository.findById(id)
                .map(payment -> Response.ok(payment).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/account/{accountId}")
    public List<Payment> getPaymentsByAccountId(@PathParam("accountId") Long accountId) {
        return paymentRepository.findByAccountId(accountId);
    }

    @POST
    public Response createPayment(Payment payment) {
        // 1. Перевірка рахунків через REST (Account Service)
        try {
            AccountServiceClient.ExistsResponse fromAccountExists =
                    accountServiceClient.checkAccountExists(payment.getFromAccountId());
            if (!fromAccountExists.exists) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Source account does not exist"))
                        .build();
            }

            AccountServiceClient.ExistsResponse toAccountExists =
                    accountServiceClient.checkAccountExists(payment.getToAccountId());
            if (!toAccountExists.exists) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Destination account does not exist"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Account service is unavailable: " + e.getMessage()))
                    .build();
        }

        // 2. Створення платежу
        payment.setStatus(Payment.PaymentStatus.PROCESSING);
        Payment created = paymentRepository.createPayment(payment);

        // 3. Створення транзакції через gRPC (Transaction Service)
        try {
            CreateTransactionRequest grpcRequest = CreateTransactionRequest.newBuilder()
                    .setAccountId(payment.getFromAccountId())
                    .setType("PAYMENT")
                    .setAmount(payment.getAmount().toString())
                    .setCurrency(payment.getCurrency())
                    .setDescription("Payment #" + created.getId() + ": " + payment.getDescription())
                    .build();

            TransactionResponse transactionResponse = transactionService
                    .createTransaction(grpcRequest)
                    .await().indefinitely();

            // Оновлення платежу з ID транзакції
            created.setTransactionId(transactionResponse.getId());
            created.setStatus(Payment.PaymentStatus.COMPLETED);
            paymentRepository.updateTransactionId(created.getId(), transactionResponse.getId());
            paymentRepository.updateStatus(created.getId(), Payment.PaymentStatus.COMPLETED);

        } catch (Exception e) {
            created.setStatus(Payment.PaymentStatus.FAILED);
            paymentRepository.updateStatus(created.getId(), Payment.PaymentStatus.FAILED);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Transaction service is unavailable: " + e.getMessage()))
                    .build();
        }

        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}/status")
    public Response updatePaymentStatus(@PathParam("id") Long id, StatusUpdateRequest request) {
        return paymentRepository.updateStatus(id, request.status)
                .map(updated -> Response.ok(updated).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    public Response deletePayment(@PathParam("id") Long id) {
        boolean deleted = paymentRepository.deletePayment(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    public static class StatusUpdateRequest {
        public Payment.PaymentStatus status;

        public StatusUpdateRequest() {
        }

        public StatusUpdateRequest(Payment.PaymentStatus status) {
            this.status = status;
        }
    }

    public static class ErrorResponse {
        public String message;

        public ErrorResponse() {
        }

        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}
