package ages.hopeful.modules.auth.service;

import ages.hopeful.config.security.jwt.JwtUtil;
import ages.hopeful.modules.auth.dto.ForgotPasswordRequest;
import ages.hopeful.modules.auth.dto.LoginRequest;
import ages.hopeful.modules.auth.dto.ResetPasswordRequest;
import ages.hopeful.modules.auth.dto.TokenResponse;
import ages.hopeful.modules.user.model.PasswordResetToken;
import ages.hopeful.modules.user.model.User;
import ages.hopeful.modules.user.repository.PasswordResetTokenRepository;
import ages.hopeful.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    public TokenResponse login(LoginRequest loginRequest) {
        try {
            
           Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            User userDetails =
                    (User) authentication.getPrincipal();

            List<String> roles = userDetails.getAuthorities()
                                .stream()
                                .map(auth -> {
                                    String role = auth.getAuthority();
                                    return role.startsWith("ROLE_") ? role : "ROLE_" + role;
                                })
                                .toList();


            UUID userId = userDetails.getId(); 

            String token = jwtUtil.generateToken(userDetails.getUsername(), roles, userId);

            return new TokenResponse(token);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("username or password Invalid");
        }
    }

    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        if (forgotPasswordRequest.getEmail() == null || forgotPasswordRequest.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Digite seu e-mail.");
        }

        User user = userRepository.findByEmail(forgotPasswordRequest.getEmail()).orElseThrow(
            () -> new IllegalArgumentException("E-mail não cadastrado.")
        );

        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUser(user);
        passwordResetToken.setToken(token);
        passwordResetToken.setExpiresAt(LocalDateTime.now().plusHours(1));
        passwordResetToken.setCreatedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(passwordResetToken);

        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        if (resetPasswordRequest.getNewPassword() == null || resetPasswordRequest.getNewPassword().isEmpty()) {
            throw new IllegalArgumentException("A nova senha é obrigatória.");
        }

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(resetPasswordRequest.getToken()).orElse(null);

        if (passwordResetToken == null || passwordResetToken.isUsed() || passwordResetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Link de recuperação inválido ou expirado");
        }

        // Validação da força da senha (exemplo simples)
        if (resetPasswordRequest.getNewPassword().length() < 8) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres.");
        }

        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(user);

        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);
    }
}
