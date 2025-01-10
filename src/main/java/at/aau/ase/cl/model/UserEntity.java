package at.aau.ase.cl.model;

import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@UserDefinition
@Table(name = "users")
public class UserEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID id;

    @Username
    @Column(name = "username", nullable = false, unique = true)
    public String username;

    @Column(name = "email", nullable = false, unique = true)
    public String email;

    @Password
    @Column(nullable = false)
    public String password;

    @Column(name = "initial_login_pending")
    public boolean initialLoginPending;

    @Embedded
    public AddressEntity address;

    @Roles
    @Column(name = "role", nullable = false)
    public String role;


    /**
     * @param identifier either email or username as both are unique
     */
    public static UserEntity findByIdentifier(String identifier) {
        return find("email = ?1 or username = ?1", identifier).firstResult();
    }
}
