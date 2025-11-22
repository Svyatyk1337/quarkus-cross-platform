package ua.edu.university.resource;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import ua.edu.university.client.*;
import ua.edu.university.model.*;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Path("/")
public class WebResource {

    @Inject
    Template index;

    @Inject
    Template users;

    @Inject
    Template accounts;

    @Inject
    Template transactions;

    @Inject
    Template payments;

    @Inject
    @RestClient
    UserServiceClient userServiceClient;

    @Inject
    @RestClient
    AccountServiceClient accountServiceClient;

    @Inject
    @RestClient
    TransactionServiceClient transactionServiceClient;

    @Inject
    @RestClient
    PaymentServiceClient paymentServiceClient;

    @Inject
    JsonWebToken jwt;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Authenticated
    public TemplateInstance index() {
        String username = jwt.getName();
        return index.data("username", username);
    }

    @GET
    @Path("/users")
    @Produces(MediaType.TEXT_HTML)
    @Authenticated
    public TemplateInstance users() {
        try {
            List<User> userList = userServiceClient.getAllUsers();
            String username = jwt.getName();
            return users.data("users", userList).data("username", username);
        } catch (Exception e) {
            return users.data("error", "Failed to load users: " + e.getMessage());
        }
    }

    @GET
    @Path("/accounts")
    @Produces(MediaType.TEXT_HTML)
    @Authenticated
    public TemplateInstance accounts() {
        try {
            List<Account> accountList = accountServiceClient.getAllAccounts();
            String username = jwt.getName();
            return accounts.data("accounts", accountList).data("username", username);
        } catch (Exception e) {
            return accounts.data("error", "Failed to load accounts: " + e.getMessage());
        }
    }

    @GET
    @Path("/accounts/user/{userId}")
    @Produces(MediaType.TEXT_HTML)
    @Authenticated
    public TemplateInstance accountsByUser(@PathParam("userId") Long userId) {
        try {
            List<Account> accountList = accountServiceClient.getAccountsByUserId(userId);
            String username = jwt.getName();
            return accounts.data("accounts", accountList).data("userId", userId).data("username", username);
        } catch (Exception e) {
            return accounts.data("error", "Failed to load accounts: " + e.getMessage());
        }
    }

    @GET
    @Path("/transactions")
    @Produces(MediaType.TEXT_HTML)
    @Authenticated
    public TemplateInstance transactions() {
        try {
            List<Transaction> transactionList = transactionServiceClient.getAllTransactions();
            String username = jwt.getName();
            return transactions.data("transactions", transactionList).data("username", username);
        } catch (Exception e) {
            return transactions.data("error", "Failed to load transactions: " + e.getMessage());
        }
    }

    @GET
    @Path("/transactions/account/{accountId}")
    @Produces(MediaType.TEXT_HTML)
    @Authenticated
    public TemplateInstance transactionsByAccount(@PathParam("accountId") Long accountId) {
        try {
            List<Transaction> transactionList = transactionServiceClient.getTransactionsByAccountId(accountId);
            String username = jwt.getName();
            return transactions.data("transactions", transactionList).data("accountId", accountId).data("username", username);
        } catch (Exception e) {
            return transactions.data("error", "Failed to load transactions: " + e.getMessage());
        }
    }

    @GET
    @Path("/payments")
    @Produces(MediaType.TEXT_HTML)
    @Authenticated
    public TemplateInstance payments() {
        try {
            List<Payment> paymentList = paymentServiceClient.getAllPayments();
            String username = jwt.getName();
            return payments.data("payments", paymentList).data("username", username);
        } catch (Exception e) {
            return payments.data("error", "Failed to load payments: " + e.getMessage());
        }
    }

    @GET
    @Path("/payments/account/{accountId}")
    @Produces(MediaType.TEXT_HTML)
    @Authenticated
    public TemplateInstance paymentsByAccount(@PathParam("accountId") Long accountId) {
        try {
            List<Payment> paymentList = paymentServiceClient.getPaymentsByAccountId(accountId);
            String username = jwt.getName();
            return payments.data("payments", paymentList).data("accountId", accountId).data("username", username);
        } catch (Exception e) {
            return payments.data("error", "Failed to load payments: " + e.getMessage());
        }
    }
}
