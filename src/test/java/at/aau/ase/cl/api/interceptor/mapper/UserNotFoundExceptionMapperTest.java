package at.aau.ase.cl.api.interceptor.mapper;

import at.aau.ase.cl.api.interceptor.exceptions.UserNotFoundException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class UserNotFoundExceptionMapperTest {
    @Inject
    UserNotFoundExceptionMapper mapper;

    @Test
    void testToResponse() {
        UserNotFoundException exception = new UserNotFoundException("Test invalid argument");

        Response response = mapper.toResponse(exception);

        assertNotNull(response, "Response must not be null");
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus(), "Status code should be 404 (NOT_FOUND)");
    }
}
