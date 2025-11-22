package ua.edu.university.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import ua.edu.university.model.User;

import java.util.List;

@Path("/api/users")
@RegisterRestClient(configKey = "user-service")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface UserServiceClient {

    @GET
    List<User> getAllUsers();

    @GET
    @Path("/{id}")
    User getUserById(@PathParam("id") Long id);

    @POST
    User createUser(User user);
}
