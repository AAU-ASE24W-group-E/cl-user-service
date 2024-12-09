package at.aau.ase.cl.api;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/user-only")
public class UserOnlyResource {

    @GET
    @RolesAllowed("USER")
    public Response getUserOnlyResource() {
        return Response.ok("This is a user-only resource").build();
    }
}