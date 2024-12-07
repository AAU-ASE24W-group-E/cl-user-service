package at.aau.ase.cl.service;

import at.aau.ase.cl.model.AddressEntity;
import at.aau.ase.cl.model.UserEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class UserServiceTest {

    @Inject
    UserService userService;

    @Test
    public void testCreateUser() {
        UserEntity user = new UserEntity();
        user.address = null;
        user.firstName = "first name";
        user.lastName = "last name";
        user.email = "email1";
        user.username = "username1";

        UserEntity createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertNull(createdUser.address);
        assertEquals(user.firstName, createdUser.firstName);
        assertEquals(user.lastName, createdUser.lastName);
        assertEquals(user.email, createdUser.email);
        assertEquals(user.username, createdUser.username);
        assertNotNull(createdUser.id);
    }

    @Test
    public void testAddAddressToUser() {
        UserEntity user = new UserEntity();
        user.address = null;
        user.firstName = "first name";
        user.lastName = "last name";
        user.email = "email2";
        user.username = "username2";

        UserEntity createdUser = userService.createUser(user);
        assertNotNull(createdUser);
        assertNull(createdUser.address);

        AddressEntity address = new AddressEntity();
        address.street = "Street";
        address.city = "City";
        address.postalCode = "9020";
        address.countryCode = "AT";

        UserEntity userWithAddress = userService.addAddressToUser(createdUser.id, address);

        assertNotNull(userWithAddress);
        assertNotNull(userWithAddress.address);
        assertEquals("Street", userWithAddress.address.street);
        assertEquals("City", userWithAddress.address.city);
        assertEquals("9020", userWithAddress.address.postalCode);
        assertEquals("AT", userWithAddress.address.countryCode);
    }

    @Test
    public void testAddUserWhichAlreadyExists() {
        UserEntity user = new UserEntity();
        user.address = null;
        user.firstName = "first name";
        user.lastName = "last name";
        user.email = "email3";
        user.username = "username3";

        UserEntity createdUser = userService.createUser(user);
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(createdUser));
    }


    @Test
    public void testGetUserThatDoesNotExist() {
        assertThrows(NotFoundException.class, () -> userService.getUserById(UUID.randomUUID()));
    }

    @Test
    public void testAddressToUserThatDoesNotExist() {
        assertThrows(NotFoundException.class, () -> userService.addAddressToUser(UUID.randomUUID(), null));
    }
}
