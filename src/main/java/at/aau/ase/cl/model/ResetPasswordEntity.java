package at.aau.ase.cl.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "reset_password_tokens")
public class ResetPasswordEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID id;

    @Column(name = "user_id", nullable = false)
    public UUID userId;

    @Column(name = "reset_token", nullable = false, unique = true)
    public UUID token;

    @Column(name = "used", nullable = false)
    public boolean used = false;

    public static ResetPasswordEntity findUnusedTokenByUserId(UUID userId) {
        return find("userId = ?1 and used = false", userId).firstResult();
    }

    public static ResetPasswordEntity findByToken(String token) {
        return find("token = ?1 and used = false", UUID.fromString(token)).firstResult();
    }
}
