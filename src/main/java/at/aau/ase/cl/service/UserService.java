package at.aau.ase.cl.service;

import at.aau.ase.cl.model.UserEntity;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.UUID;

@ApplicationScoped
public class UserService {

    @Transactional
    public UserEntity createUser(UserEntity user) {
        // create user
        user.id = UUID.randomUUID();
        user.persist();
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
}
