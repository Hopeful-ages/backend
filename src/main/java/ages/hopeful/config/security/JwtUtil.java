package ages.hopeful.config.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

@Component
public class JwtUtil {

    private static final KeyPair keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
    private static final PrivateKey PRIVATE_KEY = keyPair.getPrivate();
    private static final PublicKey PUBLIC_KEY = keyPair.getPublic();
    private static final long EXPIRATION = 86400000;

    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(PRIVATE_KEY, SignatureAlgorithm.RS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(PUBLIC_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getRoleFromToken(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(PUBLIC_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
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
