package ages.hopeful.config.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtUtil {

    private static final KeyPair keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
    private static final PrivateKey PRIVATE_KEY = keyPair.getPrivate();
    private static final PublicKey PUBLIC_KEY = keyPair.getPublic();
    private static final long EXPIRATION = 86400000;

    public String generateToken(String username, List<String> roles, UUID userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("roles", roles)
                .claim("email", username) // adiciona o email

                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(PRIVATE_KEY, SignatureAlgorithm.RS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
    return (String) Jwts.parserBuilder()
            .setSigningKey(PUBLIC_KEY)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .get("email");
}

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        return (List<String>) Jwts.parserBuilder()
                .setSigningKey(PUBLIC_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("roles"); // pega o claim "roles"
    }
    public UUID getUserIdFromToken(String token) {
        String id = Jwts.parserBuilder()
                .setSigningKey(PUBLIC_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return UUID.fromString(id);
    }

    public void validateToken(String token) throws Exception {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(PUBLIC_KEY)
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new Exception("Expired token");
        } catch (JwtException | IllegalArgumentException e) {
            throw new Exception("Invalid token");
        }
    }
}
