package ua.edu.university.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import ua.edu.university.model.Payment;

import java.util.List;

@Path("/api/payments")
@RegisterRestClient(configKey = "payment-service")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface PaymentServiceClient {

    @GET
    List<Payment> getAllPayments();

    @GET
    @Path("/{id}")
    Payment getPaymentById(@PathParam("id") Long id);

    @GET
    @Path("/account/{accountId}")
    List<Payment> getPaymentsByAccountId(@PathParam("accountId") Long accountId);

    @POST
    Payment createPayment(Payment payment);
}
