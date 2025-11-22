package ua.edu.university.resource;

import io.quarkus.security.Authenticated;
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
@Authenticated
public class TransactionResource {

    @Inject
    TransactionRepository transactionRepository;

    @Inject
    @RestClient
    AccountServiceClient accountServiceClient;

    @GET
    public List<Transaction> getAllTransactions() {
        return transactionRepository.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getTransactionById(@PathParam("id") Long id) {
        Transaction transaction = transactionRepository.findById(id);
        if (transaction != null) {
            return Response.ok(transaction).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/account/{accountId}")
    public List<Transaction> getTransactionsByAccountId(@PathParam("accountId") Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    @POST
    @jakarta.transaction.Transactional
    public Response createTransaction(Transaction transaction) {
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

        transactionRepository.persist(transaction);
        return Response.status(Response.Status.CREATED).entity(transaction).build();
    }

    @PUT
    @Path("/{id}/status")
    @jakarta.transaction.Transactional
    public Response updateTransactionStatus(@PathParam("id") Long id, StatusUpdateRequest request) {
        Transaction transaction = transactionRepository.findById(id);
        if (transaction != null) {
            transaction.setStatus(request.status);
            return Response.ok(transaction).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    @jakarta.transaction.Transactional
    public Response deleteTransaction(@PathParam("id") Long id) {
        boolean deleted = transactionRepository.deleteById(id);
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
