package at.aau.ase.cl.api.interceptor.mapper;

import at.aau.ase.cl.api.interceptor.exceptions.EmailAlreadyExistsException;
import at.aau.ase.cl.api.interceptor.exceptions.NotFoundException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class NotFoundExceptionMapperTest {
    @Inject
    NotFoundExceptionMapper mapper;

    @Test
    void testToResponse() {
        NotFoundException exception = new NotFoundException("Test invalid argument");

        Response response = mapper.toResponse(exception);

        assertNotNull(response, "Response must not be null");
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus(), "Status code should be 404 (NOT_FOUND)");
    }
}
