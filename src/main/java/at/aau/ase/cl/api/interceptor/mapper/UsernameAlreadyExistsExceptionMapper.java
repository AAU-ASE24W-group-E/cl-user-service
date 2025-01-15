package at.aau.ase.cl.api.interceptor.mapper;

import at.aau.ase.cl.api.interceptor.ErrorResponse;
import at.aau.ase.cl.api.interceptor.exceptions.UsernameAlreadyExistsException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.ExceptionMapper;

@Provider
@jakarta.ws.rs.Produces(MediaType.APPLICATION_JSON)
public class UsernameAlreadyExistsExceptionMapper implements ExceptionMapper<UsernameAlreadyExistsException> {
    @Override
    public Response toResponse(UsernameAlreadyExistsException exception) {
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(ErrorResponse.of(exception))
                .build();
    }
}