package at.aau.ase.cl.service;

import at.aau.ase.cl.api.model.Address;
import at.aau.ase.cl.api.model.User;
import at.aau.ase.cl.event.UserEvent;
import at.aau.ase.cl.mapper.AddressMapper;
import at.aau.ase.cl.mapper.UserMapper;
import at.aau.ase.cl.model.AddressEntity;
import at.aau.ase.cl.model.UserEntity;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class EventServiceTest {

    @Inject
    @Any
    InMemoryConnector connector;

    InMemorySink<UserEvent> userEventSink;

    @Inject
    UserService userService;

    @Inject
    EventService eventService;

    @BeforeEach
    void setUp() {
        userEventSink = connector.sink(EventService.USER_EVENT_CHANNEL);
        userEventSink.clear();
    }

    @Test
    void createUserShouldEmitEvent() {
        UserEntity user = createTestUserEntity();

        userService.createUser(user);

        assertEquals(1, userEventSink.received().size());
        var event = userEventSink.received().getFirst().getPayload();
        assertEquals(user.id, event.id());
        assertEquals(user.username, event.username());
        assertEquals(user.address.latitude, event.latitude());
        assertEquals(user.address.longitude, event.longitude());
    }

    @Test
    void addAddressShouldEmitEvent() {
        UserEntity user = createTestUserEntity();
        userService.createUser(user);

        AddressEntity address = AddressMapper.INSTANCE.map(
                new Address(user.address.latitude / -2, user.address.longitude / -3));
        userService.addAddressToUser(user.id, address);

        assertEquals(2, userEventSink.received().size());
        var event = userEventSink.received().getLast().getPayload();
        assertEquals(user.id, event.id());
        assertEquals(user.username, event.username());
        assertEquals(address.latitude, event.latitude());
        assertEquals(address.longitude, event.longitude());
    }

    @Test
    void updateAddressShouldEmitEvent() {
        UserEntity user = createTestUserEntity();
        userService.createUser(user);

        AddressEntity address = AddressMapper.INSTANCE.map(
                new Address(user.address.latitude / -2, user.address.longitude / -3));
        userService.updateAddress(user.id, address);

        assertEquals(2, userEventSink.received().size());
        var event = userEventSink.received().getLast().getPayload();
        assertEquals(user.id, event.id());
        assertEquals(user.username, event.username());
        assertEquals(address.latitude, event.latitude());
        assertEquals(address.longitude, event.longitude());
    }

    @Test
    void sendUserEventShouldBeSkippedForNullUser() {
        eventService.sendUserEvent(null);

        assertTrue(userEventSink.received().isEmpty());
    }

    @Test
    void sendUserEventShouldBeSkippedForUserWithNullId() {
        var user = createTestUserEntity();
        user.id = null;

        eventService.sendUserEvent(user);

        assertTrue(userEventSink.received().isEmpty());
    }

    @Test
    void sendUserEventShouldBeSkippedForUserWithoutAddress() {
        var user = createTestUserEntity();
        user.id = UUID.randomUUID();
        user.address = null;

        eventService.sendUserEvent(user);

        assertTrue(userEventSink.received().isEmpty());
    }

    UserEntity createTestUserEntity() {
        long seed = System.nanoTime();
        return UserMapper.INSTANCE.map(new User(
                "test-" + seed + "@me",
                "test" + seed,
                new Address(46.5, 15.5), "test" + seed, null));
    }


}