package ages.hopeful.service;

import ages.hopeful.config.security.JwtUtil;
import ages.hopeful.dto.LoginRequest;
import ages.hopeful.dto.TokenResponse;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final JdbcTemplate jdbcTemplate;
    private final JwtUtil jwtUtil;

    public TokenResponse login(LoginRequest loginRequest) {
        String sql = "SELECT nome, senha FROM usuario WHERE nome = ?";
        try {
            var user = jdbcTemplate.queryForMap(sql, loginRequest.getUsername());

            String hashedPassword = (String) user.get("senha");

            if (!BCrypt.checkpw(loginRequest.getPassword(), hashedPassword)) {
                return null;
            }

            String token = jwtUtil.generateToken((String) user.get("nome"));
            return new TokenResponse(token);

        } catch (Exception e) {
            return null;
        }
    }
}
