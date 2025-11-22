package ua.edu.university.resource;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import ua.edu.university.client.UserServiceClient;
import ua.edu.university.model.Account;

import java.math.BigDecimal;
import java.util.List;

@Path("/api/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class AccountResource {

    @Inject
    @RestClient
    UserServiceClient userServiceClient;

    @GET
    public List<Account> getAllAccounts() {
        return Account.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getAccountById(@PathParam("id") Long id) {
        Account account = Account.findById(id);
        if (account == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(account).build();
    }

    @GET
    @Path("/user/{userId}")
    public List<Account> getAccountsByUserId(@PathParam("userId") Long userId) {
        return Account.findByUserId(userId);
    }

    @POST
    @Transactional
    public Response createAccount(Account account) {
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

        account.persist();
        return Response.status(Response.Status.CREATED).entity(account).build();
    }

    @PUT
    @Path("/{id}/balance")
    @Transactional
    public Response updateBalance(@PathParam("id") Long id, BalanceUpdateRequest request) {
        Account account = Account.findById(id);
        if (account == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        account.setBalance(request.newBalance);
        return Response.ok(account).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteAccount(@PathParam("id") Long id) {
        boolean deleted = Account.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/{id}/exists")
    public Response checkAccountExists(@PathParam("id") Long id) {
        boolean exists = Account.findById(id) != null;
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
