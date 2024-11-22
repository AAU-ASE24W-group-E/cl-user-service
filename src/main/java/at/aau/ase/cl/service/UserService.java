package at.aau.ase.cl.service;

import at.aau.ase.cl.model.AddressEntity;
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
        if (UserEntity.find("email", user.email).firstResult() != null) {
            throw new IllegalArgumentException("A user with this email already exists: " + user.email);
        }
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

    @Transactional
    public UserEntity addAddressToUser(UUID userId, AddressEntity address) {
        UserEntity user = getUserById(userId);

        if (user == null) {
            Log.debugf("User with id %s not found", userId);
            throw new NotFoundException("User with id " + userId + " not found");
        }

        if(!address.isPersistent()){
            address.persist();
            Log.debugf("Address persisted: %s", address);
        }else {
            Log.debugf("Address already there: %s", address);
        }

        user.address = address;
        user.persist();
        Log.debugf("Added address %s to User with id %s found: %s", address, userId);
        return user;
    }

}
