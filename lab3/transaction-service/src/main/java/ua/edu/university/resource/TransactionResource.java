package ua.edu.university.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import ua.edu.university.client.AccountServiceClient;
import ua.edu.university.model.Transaction;
import ua.edu.university.repository.TransactionRepository;

import java.util.List;

@Path("/api/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionResource {

    @Inject
    TransactionRepository transactionRepository;

    @Inject
    @RestClient
    AccountServiceClient accountServiceClient;

    @GET
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getTransactionById(@PathParam("id") Long id) {
        return transactionRepository.findById(id)
                .map(transaction -> Response.ok(transaction).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/account/{accountId}")
    public List<Transaction> getTransactionsByAccountId(@PathParam("accountId") Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    @POST
    public Response createTransaction(Transaction transaction) {
        // Перевірка чи існує рахунок через REST Client
        try {
            AccountServiceClient.ExistsResponse response =
                    accountServiceClient.checkAccountExists(transaction.getAccountId());
            if (!response.exists) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Account with id " + transaction.getAccountId() + " does not exist"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Account service is unavailable"))
                    .build();
        }

        Transaction created = transactionRepository.createTransaction(transaction);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}/status")
    public Response updateTransactionStatus(@PathParam("id") Long id, StatusUpdateRequest request) {
        return transactionRepository.updateStatus(id, request.status)
                .map(updated -> Response.ok(updated).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    public Response deleteTransaction(@PathParam("id") Long id) {
        boolean deleted = transactionRepository.deleteTransaction(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    public static class StatusUpdateRequest {
        public Transaction.TransactionStatus status;

        public StatusUpdateRequest() {
        }

        public StatusUpdateRequest(Transaction.TransactionStatus status) {
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
