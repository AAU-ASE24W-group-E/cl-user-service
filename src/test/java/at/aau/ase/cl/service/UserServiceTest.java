package at.aau.ase.cl.service;

import at.aau.ase.cl.model.AddressEntity;
import at.aau.ase.cl.model.UserEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class UserServiceTest {

    @Inject
    UserService userService;

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
        assertEquals(user.firstName, createdUser.firstName);
        assertEquals(user.lastName, createdUser.lastName);
        assertNotNull(createdUser.id);
    }

    @Test
    @Transactional
    public void testAddAddressToUser() {
        UserEntity user = new UserEntity();
        user.address = null;
        user.firstName = "first name";
        user.lastName = "last name";

        UserEntity createdUser = userService.createUser(user);
        assertNotNull(createdUser);
        assertNull(createdUser.address);

        AddressEntity address = new AddressEntity();
        address.id = null;
        address.street = "Street";
        address.city = "City";
        address.postalCode = "9020";
        address.country = "Country";

        UserEntity userWithAddress = userService.addAddressToUser(createdUser.id, address);

        assertNotNull(userWithAddress);
        assertNotNull(userWithAddress.address);
        assertEquals(address.id, userWithAddress.address.id);
        assertEquals("Street", userWithAddress.address.street);
        assertEquals("City", userWithAddress.address.city);
        assertEquals("9020", userWithAddress.address.postalCode);
        assertEquals("Country", userWithAddress.address.country);
    }
}