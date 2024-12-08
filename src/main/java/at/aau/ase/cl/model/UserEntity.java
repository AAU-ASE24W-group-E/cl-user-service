package at.aau.ase.cl.model;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users") //user table may be already in use
public class UserEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID id;

    @Column(name = "email", nullable = false, unique = true)
    public String email;

    @Column(name = "username", nullable = false, unique = true)
    public String username;

    @Embedded
    public AddressEntity address;

    @Column(nullable = false)
    public String password;


    //TODO: Add Location with Latitude Longitude

    public List<UserEntity> findByUserId(UUID userId) {

        return find("userId", Sort.by("title"), userId).list();
    }

    /**
     * @param identifier either email or username as both are unique
     */
    public static UserEntity findByIdentifier(String identifier) {
        return find("email = ?1 or username = ?1", identifier).firstResult();
    }
}
