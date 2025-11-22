package ua.edu.university.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import ua.edu.university.model.Account;

import java.util.List;

@Path("/api/accounts")
@RegisterRestClient(configKey = "account-service")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AccountServiceClient {

    @GET
    List<Account> getAllAccounts();

    @GET
    @Path("/{id}")
    Account getAccountById(@PathParam("id") Long id);

    @GET
    @Path("/user/{userId}")
    List<Account> getAccountsByUserId(@PathParam("userId") Long userId);

    @POST
    Account createAccount(Account account);
}
