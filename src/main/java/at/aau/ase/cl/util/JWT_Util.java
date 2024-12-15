package at.aau.ase.cl.util;

import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Arrays;
import java.util.HashSet;

@ApplicationScoped
public class JWT_Util {

    @Inject
    JWTParser jwtParser;

    public static String generateToken(String userId, String username, String role) {
        return Jwt.issuer("user-service")
                .subject(userId)
                .groups(new HashSet<>(Arrays.asList("USER")))
                .claim("role", role)
                .claim("username", username)
                .expiresAt(System.currentTimeMillis() / 1000 + 3600)
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
}
