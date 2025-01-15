package at.aau.ase.cl.api.interceptor;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class ErrorResponseTest {
    @Test
    void testOf_withException() {
        Throwable exception = new IllegalArgumentException("Test exception message");

        ErrorResponse response = ErrorResponse.of(exception);

        assertEquals("IllegalArgumentException", response.type(), "Exception type should match");
        assertEquals("Test exception message", response.message(), "Exception message should match");
    }
}