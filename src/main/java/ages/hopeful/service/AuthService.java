package ages.hopeful.service;

import ages.hopeful.config.security.JwtUtil;
import ages.hopeful.dto.LoginRequest;
import ages.hopeful.dto.TokenResponse;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final JdbcTemplate jdbcTemplate;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public TokenResponse login(LoginRequest loginRequest) {
        String sql = "SELECT nome, email, senha FROM usuario WHERE email = ?";
        try {
            var user = jdbcTemplate.queryForMap(sql, loginRequest.getUsername());

            String storedPassword = (String) user.get("senha");

            if (!passwordEncoder.matches(loginRequest.getPassword(), storedPassword)) {
                return null;
            }

            String token = jwtUtil.generateToken((String) user.get("email"));
            return new TokenResponse(token);

        } catch (Exception e) {
            return null;
        }
    }
}
