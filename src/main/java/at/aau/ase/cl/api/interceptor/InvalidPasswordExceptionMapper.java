package at.aau.ase.cl.api.interceptor;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@jakarta.ws.rs.Produces(MediaType.APPLICATION_JSON)
public class InvalidPasswordExceptionMapper implements ExceptionMapper<InvalidPasswordException> {
    @Override
    public Response toResponse(InvalidPasswordException exception) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .type(MediaType.APPLICATION_JSON)
                .entity(ErrorResponse.of(exception))
                .build();
    }
}