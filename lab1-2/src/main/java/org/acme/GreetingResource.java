package org.acme;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @PermitAll
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Quarkus REST";
    }

    @GET
    @Path("/secured")
    @Authenticated
    @Produces(MediaType.TEXT_PLAIN)
    public String secured() {
        return "This is a secured endpoint. Hello, " + securityIdentity.getPrincipal().getName();
    }

    @GET
    @Path("/admin")
    @RolesAllowed("admin")
    @Produces(MediaType.TEXT_PLAIN)
    public String admin() {
        return "This is an admin endpoint. Hello, " + securityIdentity.getPrincipal().getName();
    }

    @GET
    @Path("/user")
    @RolesAllowed("user")
    @Produces(MediaType.TEXT_PLAIN)
    public String user() {
        return "This is a user endpoint. Hello, " + securityIdentity.getPrincipal().getName();
    }
}
