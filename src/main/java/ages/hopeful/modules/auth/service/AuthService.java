package ages.hopeful.modules.auth.service;

import ages.hopeful.modules.auth.dto.LoginRequest;
import ages.hopeful.modules.auth.dto.TokenResponse;
import ages.hopeful.modules.auth.entity.User;
import ages.hopeful.modules.auth.repository.UserRepository;
import ages.hopeful.shared.config.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public TokenResponse login(LoginRequest loginRequest) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(loginRequest.getUsername());
            
            if (userOpt.isEmpty()) {
                return null;
            }
            
            User user = userOpt.get();
            
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return null;
            }

            String token = jwtUtil.generateToken(user.getEmail());
            
            return new TokenResponse(token);

        } catch (Exception e) {
            return null;
        }
    }
}
