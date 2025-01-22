package at.aau.ase.cl.service;

import at.aau.ase.cl.api.interceptor.exceptions.NotFoundException;
import at.aau.ase.cl.model.ResetPasswordEntity;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;

import java.util.UUID;

@ApplicationScoped
public class ResetPasswordService {

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public ResetPasswordEntity savePasswordResetToken(UUID userId, UUID token) {
        ResetPasswordEntity resetPasswordEntity = new ResetPasswordEntity();

        try {
            resetPasswordEntity.userId = userId;
            resetPasswordEntity.token = token;

            entityManager.persist(resetPasswordEntity);
            entityManager.flush();
        } catch (PersistenceException e) {
            throw new IllegalArgumentException("An error occurred while saving the password reset token: ", e);
        }

        return resetPasswordEntity;
    }

    @Transactional
    public ResetPasswordEntity getResetPasswordEntityByToken(String token) {
        ResetPasswordEntity resetPasswordEntity = ResetPasswordEntity.findByToken(token);
        if (resetPasswordEntity == null) {
            Log.debugf("Reset Token %s not found or used", token);
            throw new NotFoundException("Reset Token " + token + " not found or used");
        }
        Log.debugf("Valid reset token found: %s", resetPasswordEntity);
        return resetPasswordEntity;
    }


    @Transactional
    public void invalidateToken(ResetPasswordEntity resetPasswordEntity) {
        ResetPasswordEntity managedEntity = entityManager.merge(resetPasswordEntity);
        entityManager.remove(managedEntity);
    }
}
