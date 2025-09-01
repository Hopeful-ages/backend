package ages.hopeful.modules.auth.service;

import ages.hopeful.config.security.jwt.JwtUtil;
import ages.hopeful.modules.auth.dto.LoginRequest;
import ages.hopeful.modules.auth.dto.TokenResponse;
import ages.hopeful.modules.user.model.User;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public TokenResponse login(LoginRequest loginRequest) {
        try {
            
           Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            // cast para seu User
            User userDetails =
                    (User) authentication.getPrincipal();

            String role = userDetails.getAuthorities().stream()
                        .findFirst()
                        .map(auth -> auth.getAuthority())
                        .orElse("ROLE_USER");

            UUID userId = userDetails.getId(); // agora sim tem o UUID

            String token = jwtUtil.generateToken(userDetails.getUsername(), role, userId);

            return new TokenResponse(token);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("username or password Invalid");
        }
    }
}
