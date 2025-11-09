package ua.edu.university.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import ua.edu.university.client.UserServiceClient;
import ua.edu.university.model.Account;
import ua.edu.university.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.List;

@Path("/api/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountResource {

    @Inject
    AccountRepository accountRepository;

    @Inject
    @RestClient
    UserServiceClient userServiceClient;

    @GET
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getAccountById(@PathParam("id") Long id) {
        return accountRepository.findById(id)
                .map(account -> Response.ok(account).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/user/{userId}")
    public List<Account> getAccountsByUserId(@PathParam("userId") Long userId) {
        return accountRepository.findByUserId(userId);
    }

    @POST
    public Response createAccount(Account account) {
        // Перевірка чи існує користувач через REST Client
        try {
            UserServiceClient.ExistsResponse response = userServiceClient.checkUserExists(account.getUserId());
            if (!response.exists) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("User with id " + account.getUserId() + " does not exist"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("User service is unavailable"))
                    .build();
        }

        Account created = accountRepository.createAccount(account);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}/balance")
    public Response updateBalance(@PathParam("id") Long id, BalanceUpdateRequest request) {
        return accountRepository.updateBalance(id, request.newBalance)
                .map(updated -> Response.ok(updated).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    public Response deleteAccount(@PathParam("id") Long id) {
        boolean deleted = accountRepository.deleteAccount(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/{id}/exists")
    public Response checkAccountExists(@PathParam("id") Long id) {
        boolean exists = accountRepository.existsById(id);
        return Response.ok().entity(new ExistsResponse(exists)).build();
    }

    public static class BalanceUpdateRequest {
        public BigDecimal newBalance;

        public BalanceUpdateRequest() {
        }

        public BalanceUpdateRequest(BigDecimal newBalance) {
            this.newBalance = newBalance;
        }
    }

    public static class ExistsResponse {
        public boolean exists;

        public ExistsResponse() {
        }

        public ExistsResponse(boolean exists) {
            this.exists = exists;
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
