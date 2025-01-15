package at.aau.ase.cl.api.interceptor.mapper;

import at.aau.ase.cl.api.interceptor.exceptions.NotFoundException;
import at.aau.ase.cl.api.interceptor.exceptions.UsernameAlreadyExistsException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class UsernameAlreadyExistsExceptionMapperTest {
    @Inject
    UsernameAlreadyExistsExceptionMapper mapper;

    @Test
    void testToResponse() {
        UsernameAlreadyExistsException exception = new UsernameAlreadyExistsException("Test invalid argument");

        Response response = mapper.toResponse(exception);

        assertNotNull(response, "Response must not be null");
        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus(), "Status code should be 409 (CONFLICT)");
    }
}
