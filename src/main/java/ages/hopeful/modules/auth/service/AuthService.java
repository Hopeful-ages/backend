package ages.hopeful.modules.auth.service;

import ages.hopeful.config.security.jwt.JwtUtil;
import ages.hopeful.modules.auth.dto.LoginRequest;
import ages.hopeful.modules.auth.dto.TokenResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
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

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String role = userDetails.getAuthorities().stream()
                         .findFirst()
                         .map(auth -> auth.getAuthority())
                         .orElse("ROLE_USER"); 

            String token = jwtUtil.generateToken(userDetails.getUsername(), role);

            return new TokenResponse(token);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("username or password Invalid");
        }
    }
}
