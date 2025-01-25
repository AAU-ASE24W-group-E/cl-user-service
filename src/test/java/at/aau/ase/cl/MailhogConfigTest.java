package at.aau.ase.cl;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Disabled("learning test")
@QuarkusTest
public class MailhogConfigTest {

    @Inject
    Mailer mailer;

    @Test
    public void testMailhogConfiguration() {
        assertDoesNotThrow(() -> {
            mailer.send(Mail.withText(
                    "test@example.com",
                    "Test Email",
                    "This is a test email to verify MailHog configuration."
            ));
        }, "The email sending through MailHog configuration failed.");
    }
}