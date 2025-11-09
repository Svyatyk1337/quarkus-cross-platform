package ua.edu.university.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/users")
@RegisterRestClient(configKey = "user-service")
public interface UserServiceClient {

    @GET
    @Path("/{id}/exists")
    ExistsResponse checkUserExists(@PathParam("id") Long id);

    class ExistsResponse {
        public boolean exists;

        public ExistsResponse() {
        }

        public ExistsResponse(boolean exists) {
            this.exists = exists;
        }
    }
}
