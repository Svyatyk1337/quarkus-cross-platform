package ua.edu.university.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/accounts")
@RegisterRestClient(configKey = "account-service")
public interface AccountServiceClient {

    @GET
    @Path("/{id}/exists")
    ExistsResponse checkAccountExists(@PathParam("id") Long id);

    class ExistsResponse {
        public boolean exists;

        public ExistsResponse() {
        }

        public ExistsResponse(boolean exists) {
            this.exists = exists;
        }
    }
}
