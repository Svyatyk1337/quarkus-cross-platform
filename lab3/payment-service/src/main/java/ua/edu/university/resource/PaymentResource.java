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
        return paymentRepository.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getPaymentById(@PathParam("id") Long id) {
        Payment payment = paymentRepository.findById(id);
        if (payment != null) {
            return Response.ok(payment).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/account/{accountId}")
    public List<Payment> getPaymentsByAccountId(@PathParam("accountId") Long accountId) {
        return paymentRepository.findByAccountId(accountId);
    }

    @POST
    @jakarta.transaction.Transactional
    public Response createPayment(Payment payment) {
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

        payment.setStatus(Payment.PaymentStatus.PROCESSING);
        paymentRepository.persist(payment);

        try {
            CreateTransactionRequest grpcRequest = CreateTransactionRequest.newBuilder()
                    .setAccountId(payment.getFromAccountId())
                    .setType("PAYMENT")
                    .setAmount(payment.getAmount().toString())
                    .setCurrency(payment.getCurrency())
                    .setDescription("Payment #" + payment.getId() + ": " + payment.getDescription())
                    .build();

            TransactionResponse transactionResponse = transactionService
                    .createTransaction(grpcRequest)
                    .await().indefinitely();

            payment.setTransactionId(transactionResponse.getId());
            payment.setStatus(Payment.PaymentStatus.COMPLETED);

        } catch (Exception e) {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Transaction service is unavailable: " + e.getMessage()))
                    .build();
        }

        return Response.status(Response.Status.CREATED).entity(payment).build();
    }

    @PUT
    @Path("/{id}/status")
    @jakarta.transaction.Transactional
    public Response updatePaymentStatus(@PathParam("id") Long id, StatusUpdateRequest request) {
        Payment payment = paymentRepository.findById(id);
        if (payment != null) {
            payment.setStatus(request.status);
            return Response.ok(payment).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    @jakarta.transaction.Transactional
    public Response deletePayment(@PathParam("id") Long id) {
        boolean deleted = paymentRepository.deleteById(id);
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
