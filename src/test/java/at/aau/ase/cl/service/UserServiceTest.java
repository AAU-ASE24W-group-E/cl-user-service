package at.aau.ase.cl.service;

import at.aau.ase.cl.model.UserEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class UserServiceTest {
    @Inject
    private UserService userService;

    @Test
    @Transactional
    public void testCreateUser() {
        UserEntity user = new UserEntity();

        user.address = null;
        user.firstName = "first name";
        user.lastName = "last name";

        UserEntity createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertNull(createdUser.address);
        assertNotNull(createdUser.firstName, user.firstName);
        assertNotNull(createdUser.lastName, user.lastName);
        assertNotNull(user.id);
    }

}