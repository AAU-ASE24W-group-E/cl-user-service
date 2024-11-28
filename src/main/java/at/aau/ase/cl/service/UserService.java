package at.aau.ase.cl.service;

import at.aau.ase.cl.model.AddressEntity;
import at.aau.ase.cl.model.UserEntity;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;

import java.util.UUID;

@ApplicationScoped
public class UserService {

    @Transactional
    public UserEntity createUser(UserEntity user) {
        try {
            user.persistAndFlush();
        } catch (PersistenceException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new IllegalArgumentException("A user with this identifier already exists: " + user.email, e);
            }
            throw e;
        }
        return user;
    }

    public UserEntity getUserById(UUID id) {
        UserEntity user = UserEntity.findById(id);
        if (user == null) {
            Log.debugf("User with id %s not found", id);
            throw new NotFoundException("User with id " + id + " not found");
        }
        Log.debugf("User with id %s found: %s", id, user);
        return user;
    }

    @Transactional
    public UserEntity addAddressToUser(UUID userId, AddressEntity address) {
        UserEntity user = getUserById(userId);

        if (user == null) {
            Log.debugf("User with id %s not found", userId);
            throw new NotFoundException("User with id " + userId + " not found");
        }

        user.address = address;
        user.persistAndFlush();
        Log.debugf("Added address to User with id %s: %s", userId, user);
        return user;
    }

}
