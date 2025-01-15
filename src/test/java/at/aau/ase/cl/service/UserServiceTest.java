package at.aau.ase.cl.service;

import at.aau.ase.cl.api.interceptor.exceptions.EmailAlreadyExistsException;
import at.aau.ase.cl.api.interceptor.exceptions.NotFoundException;
import at.aau.ase.cl.api.interceptor.exceptions.UserNotFoundException;
import at.aau.ase.cl.api.interceptor.exceptions.UsernameAlreadyExistsException;
import at.aau.ase.cl.model.AddressEntity;
import at.aau.ase.cl.model.UserEntity;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class UserServiceTest {

    @Inject
    UserService userService;

    @Test
    void testCreateUser() {
        UserEntity user = new UserEntity();
        user.address = null;
        user.email = "email1";
        user.username = "username1";
        user.password = "SomePassword";

        UserEntity createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertNull(createdUser.address);
        assertEquals(user.email, createdUser.email);
        assertEquals(user.username, createdUser.username);
        assertTrue(BcryptUtil.matches("SomePassword", createdUser.password));
        assertNotNull(createdUser.id);
    }

    @Test
    void testAddAddressToUser() {
        UserEntity user = new UserEntity();
        user.address = null;
        user.email = "email2";
        user.username = "username2";
        user.password = "SomePassword";

        UserEntity createdUser = userService.createUser(user);
        assertNotNull(createdUser);
        assertNull(createdUser.address);

        AddressEntity address = new AddressEntity();
        address.latitude = 49.21303;
        address.longitude = 20.49321;

        UserEntity userWithAddress = userService.addAddressToUser(createdUser.id, address);

        assertNotNull(userWithAddress);
        assertNotNull(userWithAddress.address);
        assertEquals(address.latitude, userWithAddress.address.latitude);
        assertEquals(address.longitude, userWithAddress.address.longitude);
    }

    @Test
    void testUpdateAddressOfUser() {
        UserEntity user = new UserEntity();
        user.address = null;
        user.email = "email3";
        user.username = "username3";
        user.password = "SomePassword";

        UserEntity createdUser = userService.createUser(user);
        assertNotNull(createdUser);
        assertNull(createdUser.address);

        AddressEntity address = new AddressEntity();
        address.latitude = 49.21303;
        address.longitude = 20.49321;

        UserEntity userWithAddress = userService.addAddressToUser(createdUser.id, address);

        assertNotNull(userWithAddress);
        assertNotNull(userWithAddress.address);
        assertEquals(address.latitude, userWithAddress.address.latitude);
        assertEquals(address.longitude, userWithAddress.address.longitude);

        AddressEntity newAddress = new AddressEntity();
        newAddress.latitude = 50.21303;
        newAddress.longitude = 20.49321;

        UserEntity userWithNewAddress = userService.updateAddress(createdUser.id, newAddress);
        assertNotNull(userWithNewAddress);
        assertNotNull(userWithNewAddress.address);
        assertEquals(newAddress.latitude, userWithNewAddress.address.latitude);
        assertEquals(newAddress.longitude, userWithNewAddress.address.longitude);
    }

    @Test
    void testAddUserWhichAlreadyExists() {
        UserEntity user = new UserEntity();
        user.address = null;
        user.email = "email4";
        user.username = "username4";
        user.password = "SomePassword";

        UserEntity createdUser = userService.createUser(user);
        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(createdUser));
    }

    @Test
    void testAddUserWithExistingUsername() {
        UserEntity user = new UserEntity();
        user.address = null;
        user.email = "email20";
        user.username = "username20";
        user.password = "SomePassword";

        userService.createUser(user);

        UserEntity user2 = new UserEntity();
        user2.address = null;
        user2.email = "email21";
        user2.username = "username20";
        user2.password = "SomePassword";

        assertThrows(UsernameAlreadyExistsException.class, () -> userService.createUser(user2));
    }

    @Test
    void testSetLoginState() {
        UserEntity user = new UserEntity();
        user.address = null;
        user.email = "email5";
        user.username = "username5";
        user.password = "SomePassword";

        UserEntity createdUser = userService.createUser(user);
        assertNotNull(createdUser);
        assertNull(createdUser.address);
        assertTrue(createdUser.initialLoginPending);

        UserEntity updatedLoginStateUser = userService.updateInitialLoginState(createdUser.id);
        assertNotNull(updatedLoginStateUser);
        assertFalse(updatedLoginStateUser.initialLoginPending);
    }

    @Test
    void testGetUserByUsernameOrEmailNotExist() {
        assertThrows(UserNotFoundException.class, () -> userService.findByUsernameOrEmail("-"));
    }

    @Test
    void testGetUserThatDoesNotExist() {
        UUID randomUserId = UUID.randomUUID();

        assertThrows(NotFoundException.class, () -> userService.getUserById(randomUserId));
    }

    @Test
    void testAddressToUserThatDoesNotExist() {
        UUID randomUserId = UUID.randomUUID();

        assertThrows(NotFoundException.class, () -> userService.addAddressToUser(randomUserId, null));
    }

}
