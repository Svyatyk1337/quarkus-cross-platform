package ua.edu.university.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import ua.edu.university.model.Transaction;

import java.util.List;

@Path("/api/transactions")
@RegisterRestClient(configKey = "transaction-service")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface TransactionServiceClient {

    @GET
    List<Transaction> getAllTransactions();

    @GET
    @Path("/{id}")
    Transaction getTransactionById(@PathParam("id") Long id);

    @GET
    @Path("/account/{accountId}")
    List<Transaction> getTransactionsByAccountId(@PathParam("accountId") Long accountId);

    @POST
    Transaction createTransaction(Transaction transaction);
}
