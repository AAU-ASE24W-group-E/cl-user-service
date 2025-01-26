package at.aau.ase.cl.util;

import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Set;

@ApplicationScoped
public class JWT_Util {

    @Inject
    JWTParser jwtParser;

    public static String generateToken(String userId, String username, String role) {
        return Jwt.issuer("user-service")
                .subject(userId)
                .groups(Set.of("USER"))
                .claim("role", role)
                .claim("username", username)
//                .expiresAt(System.currentTimeMillis() / 1000 + 3600)
                .expiresAt(System.currentTimeMillis() / 1000 + 86400)
                .sign();
    }

    public boolean validateRole(String token, String requiredRole) {
        try {
            var jwt = jwtParser.parse(token);
            String role = jwt.getClaim("role");
            return role != null && role.equals(requiredRole);
        } catch (ParseException e) {
            return false;
        }
    }

    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
