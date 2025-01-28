package at.aau.ase.cl.service;

import at.aau.ase.cl.api.interceptor.exceptions.NotFoundException;
import at.aau.ase.cl.model.ResetPasswordEntity;
import at.aau.ase.cl.util.JWT_Util;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.InternalServerErrorException;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ResetPasswordService {

    @Inject
    UserService userService;

    @Inject
    Mailer mailer;

    @Transactional
    public void initiatePasswordReset(String email) {
        var user = userService.findByUsernameOrEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        String resetToken = UUID.randomUUID().toString();
        savePasswordResetToken(user.id, resetToken);

        Mail m = new Mail();
        m.setFrom("noreply@crowd-library-web.net");
        m.setTo(List.of(user.email));
        m.setText("Password reset request for user " + user.username + "!\n" +
                "Enter the following code: "+ resetToken);
        m.setSubject("Crowd Library Password reset request");
        mailer.send(m);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        ResetPasswordEntity resetTokenEntity = getResetPasswordEntityByToken(token);
        if (resetTokenEntity == null) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        var user = userService.getUserById(resetTokenEntity.userId);
        if (user == null) {
            throw new InternalServerErrorException("User not found");
        }

        String hashedNewPassword = JWT_Util.hashPassword(newPassword);
        userService.updatePassword(user.id, hashedNewPassword);

        invalidateToken(token);
    }

    @Transactional
    public ResetPasswordEntity savePasswordResetToken(UUID userId, String token) {
        ResetPasswordEntity resetPasswordEntity = new ResetPasswordEntity();
        resetPasswordEntity.userId = userId;
        resetPasswordEntity.token = token;
        resetPasswordEntity.persistAndFlush();
        return resetPasswordEntity;
    }

    @Transactional
    public ResetPasswordEntity getResetPasswordEntityByToken(String token) {
        ResetPasswordEntity resetPasswordEntity = ResetPasswordEntity.findByToken(token);
        if (resetPasswordEntity == null) {
            throw new NotFoundException("Reset token not found or already used");
        }
        return resetPasswordEntity;
    }

    @Transactional
    public void invalidateToken(String token) {
        long deletedCount = ResetPasswordEntity.delete("token", token);
        if (deletedCount == 0) {
            throw new IllegalArgumentException("No reset token found or already deleted for token: " + token);
        }
    }
}
