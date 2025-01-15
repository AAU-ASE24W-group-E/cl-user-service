package at.aau.ase.cl.api.interceptor.mapper;

import at.aau.ase.cl.api.interceptor.exceptions.InvalidPasswordException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class InvalidPasswordExceptionMapperTest {
    @Inject
    InvalidPasswordExceptionMapper mapper;

    @Test
    void testToResponse() {
        InvalidPasswordException exception = new InvalidPasswordException("Test invalid argument");

        Response response = mapper.toResponse(exception);

        assertNotNull(response, "Response must not be null");
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus(), "Status code should be 401 (UNAUTHORIZED)");
    }
}
