package at.aau.ase.cl.service;

import at.aau.ase.cl.api.interceptor.exceptions.EmailAlreadyExistsException;
import at.aau.ase.cl.api.interceptor.exceptions.NotFoundException;
import at.aau.ase.cl.api.interceptor.exceptions.UserNotFoundException;
import at.aau.ase.cl.api.interceptor.exceptions.UsernameAlreadyExistsException;
import at.aau.ase.cl.model.AddressEntity;
import at.aau.ase.cl.model.UserEntity;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;

import java.util.UUID;

@ApplicationScoped
public class UserService {

    @Transactional
    public UserEntity createUser(UserEntity user) {
        UserEntity existingEmailUser = UserEntity.find("email", user.email).firstResult();
        if (existingEmailUser != null) {
            throw new EmailAlreadyExistsException("A user with this email already exists: " + user.email);
        }

        UserEntity existingUsernameUser = UserEntity.find("username", user.username).firstResult();
        if (existingUsernameUser != null) {
            throw new UsernameAlreadyExistsException("A user with this username already exists: " + user.username);
        }

        try {
            user.initialLoginPending = true;
            user.password = BcryptUtil.bcryptHash(user.password);
            if (user.role == null || user.role.isEmpty()) {
                user.role = "USER";
            }
            user.persistAndFlush();
        } catch (PersistenceException e) {
            throw new IllegalArgumentException("A user with this identifier already exists: " + user.email, e);
        }
        return user;
    }

    @Transactional
    public UserEntity updateInitialLoginState(UUID id) {
        UserEntity user = getUserById(id);

        user.initialLoginPending = false;
        user.persistAndFlush();
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
    public UserEntity addAddressToUser(UUID userId,
                                       AddressEntity address) {
        UserEntity user = getUserById(userId);
        user.address = address;
        user.persistAndFlush();
        Log.debugf("Added address to User with id %s: %s", userId, user);
        return user;
    }

    @Transactional
    public UserEntity updateAddress(UUID userId,
                                    AddressEntity address) {
        UserEntity user = getUserById(userId);

        user.address.latitude = address.latitude;
        user.address.longitude = address.longitude;
        user.persistAndFlush();

        Log.debugf("Updated address to User with id %s: %s", userId, user);
        return user;
    }

    @Transactional
    public UserEntity findByUsernameOrEmail(String identifier) {
        UserEntity user = UserEntity.find("email = ?1 or username = ?1", identifier).firstResult();
        if (user == null) {
            Log.debugf("User with identifier %s not found", identifier);
            throw new UserNotFoundException("User with identifier " + identifier + " not found");
        }
        Log.debugf("User with identifier %s found: %s", identifier, user);
        return user;
    }
}
