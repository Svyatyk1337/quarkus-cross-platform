package ua.edu.university.resource;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ua.edu.university.model.User;
import ua.edu.university.repository.UserRepository;

import java.util.List;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class UserResource {

    @Inject
    UserRepository userRepository;

    @GET
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") Long id) {
        return userRepository.findById(id)
                .map(user -> Response.ok(user).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response createUser(User user) {
        User created = userRepository.createUser(user);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Long id, User user) {
        return userRepository.updateUser(id, user)
                .map(updated -> Response.ok(updated).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        boolean deleted = userRepository.deleteUser(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/{id}/exists")
    public Response checkUserExists(@PathParam("id") Long id) {
        boolean exists = userRepository.existsById(id);
        return Response.ok().entity(new ExistsResponse(exists)).build();
    }

    public static class ExistsResponse {
        public boolean exists;

        public ExistsResponse() {
        }

        public ExistsResponse(boolean exists) {
            this.exists = exists;
        }
    }
}
