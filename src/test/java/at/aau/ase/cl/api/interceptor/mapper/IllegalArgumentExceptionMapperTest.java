package at.aau.ase.cl.api.interceptor.mapper;

import io.quarkus.test.junit.QuarkusTest;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class IllegalArgumentExceptionMapperTest {
    @Inject
    IllegalArgumentExceptionMapper exceptionMapper;

    @Test
    void testToResponse() {
        IllegalArgumentException exception = new IllegalArgumentException("Test invalid argument");

        Response response = exceptionMapper.toResponse(exception);

        assertNotNull(response, "Response should not be null");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus(), "Status code should be 400 (BAD_REQUEST)");
    }
}