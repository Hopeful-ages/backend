package ages.hopeful.modules.auth.service;

import ages.hopeful.config.security.jwt.JwtUtil;
import ages.hopeful.modules.auth.dto.ForgotPasswordRequest;
import ages.hopeful.modules.auth.dto.LoginRequest;
import ages.hopeful.modules.auth.dto.ResetPasswordRequest;
import ages.hopeful.modules.auth.dto.TokenResponse;
import ages.hopeful.modules.user.model.PasswordResetToken;
import ages.hopeful.modules.user.model.Role;
import ages.hopeful.modules.user.model.User;
import ages.hopeful.modules.user.repository.PasswordResetTokenRepository;
import ages.hopeful.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for AuthService")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private Authentication authentication;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        
        loginRequest = new LoginRequest("john@example.com", "password123");

        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName("USER");

        user = new User();
        user.setId(userId);
        user.setEmail("john@example.com");
        user.setName("John Doe");
        user.setRole(role);
    }

    @Test
    @DisplayName("Should authenticate successfully and return token when credentials are valid")
    void shouldAuthenticateSuccessfullyAndReturnToken() {
        // Arrange
        String expectedToken = "fake-jwt-token-123";
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtUtil.generateToken(anyString(), anyList(), any(UUID.class)))
                .thenReturn(expectedToken);

        // Act
        TokenResponse response = authService.login(loginRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(expectedToken);

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(authentication, times(1)).getPrincipal();
        verify(jwtUtil, times(1)).generateToken(
                eq("john@example.com"),
                anyList(),
                eq(userId)
        );
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when authentication fails")
    void shouldThrowBadCredentialsExceptionWhenAuthenticationFails() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.login(loginRequest)
        );

        assertThat(exception.getMessage()).isEqualTo("username or password Invalid");

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, never()).generateToken(anyString(), anyList(), any(UUID.class));
    }

    @Test
    @DisplayName("Should extract user details correctly from authentication")
    void shouldExtractUserDetailsCorrectlyFromAuthentication() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtUtil.generateToken(anyString(), anyList(), any(UUID.class)))
                .thenReturn("token");

        // Act
        authService.login(loginRequest);

        // Assert
        verify(authentication, times(1)).getPrincipal();
        verify(jwtUtil, times(1)).generateToken(
                eq(user.getUsername()),
                anyList(),
                eq(user.getId())
        );
    }

    @Test
    @DisplayName("Should add ROLE_ prefix to roles that don't have it")
    void shouldAddRolePrefixToRolesThatDontHaveIt() {
        // Arrange
        Role roleWithoutPrefix = new Role();
        roleWithoutPrefix.setName("ADMIN");
        user.setRole(roleWithoutPrefix);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtUtil.generateToken(anyString(), anyList(), any(UUID.class)))
                .thenReturn("token");

        // Act
        authService.login(loginRequest);

        // Assert
        verify(jwtUtil, times(1)).generateToken(
                anyString(),
                argThat(roles -> roles.stream().allMatch(role -> role.startsWith("ROLE_"))),
                any(UUID.class)
        );
    }

    @Test
    @DisplayName("Should not duplicate ROLE_ prefix if already present")
    void shouldNotDuplicateRolePrefixIfAlreadyPresent() {
        // Arrange
        Role roleWithPrefix = new Role();
        roleWithPrefix.setName("ROLE_USER");
        user.setRole(roleWithPrefix);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtUtil.generateToken(anyString(), anyList(), any(UUID.class)))
                .thenReturn("token");

        // Act
        authService.login(loginRequest);

        // Assert
        verify(jwtUtil, times(1)).generateToken(
                anyString(),
                argThat(roles -> roles.stream().noneMatch(role -> role.equals("ROLE_ROLE_USER"))),
                any(UUID.class)
        );
    }

    @Test
    @DisplayName("Should pass correct username and password to authentication manager")
    void shouldPassCorrectUsernameAndPasswordToAuthenticationManager() {
        // Arrange
        LoginRequest customRequest = new LoginRequest("custom@example.com", "customPass456");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtUtil.generateToken(anyString(), anyList(), any(UUID.class)))
                .thenReturn("token");

        // Act
        authService.login(customRequest);

        // Assert
        verify(authenticationManager, times(1)).authenticate(
                argThat(auth -> 
                    auth instanceof UsernamePasswordAuthenticationToken &&
                    auth.getPrincipal().equals("custom@example.com") &&
                    auth.getCredentials().equals("customPass456")
                )
        );
    }

    @Test
    @DisplayName("Should generate token with user ID from authenticated user")
    void shouldGenerateTokenWithUserIdFromAuthenticatedUser() {
        // Arrange
        UUID specificUserId = UUID.randomUUID();
        user.setId(specificUserId);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtUtil.generateToken(anyString(), anyList(), any(UUID.class)))
                .thenReturn("token");

        // Act
        authService.login(loginRequest);

        // Assert
        verify(jwtUtil, times(1)).generateToken(
                anyString(),
                anyList(),
                eq(specificUserId)
        );
    }

    @Test
    @DisplayName("Should send password reset email when email is valid")
    void shouldSendPasswordResetEmailWhenEmailIsValid() {
        // Arrange
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("john@example.com");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        authService.forgotPassword(request);

        // Assert
        verify(userRepository, times(1)).findByEmail("john@example.com");
        verify(passwordResetTokenRepository, times(1)).save(any(PasswordResetToken.class));
        verify(emailService, times(1)).sendPasswordResetEmail(eq("john@example.com"), anyString());
    }

    @Test
    @DisplayName("Should throw exception when email is null in forgot password")
    void shouldThrowExceptionWhenEmailIsNullInForgotPassword() {
        // Arrange
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.forgotPassword(request)
        );

        assertThat(exception.getMessage()).isEqualTo("Digite seu e-mail.");
        verify(userRepository, never()).findByEmail(anyString());
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when email is empty in forgot password")
    void shouldThrowExceptionWhenEmailIsEmptyInForgotPassword() {
        // Arrange
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.forgotPassword(request)
        );

        assertThat(exception.getMessage()).isEqualTo("Digite seu e-mail.");
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("Should throw exception when email is not registered")
    void shouldThrowExceptionWhenEmailIsNotRegistered() {
        // Arrange
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("notfound@example.com");

        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.forgotPassword(request)
        );

        assertThat(exception.getMessage()).isEqualTo("E-mail não cadastrado.");
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("Should reset password successfully with valid token")
    void shouldResetPasswordSuccessfullyWithValidToken() {
        // Arrange
        String token = "valid-token";
        String newPassword = "newPassword123";
        String encodedPassword = "encoded-password";

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken(token);
        request.setNewPassword(newPassword);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setUsed(false);
        resetToken.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

        // Act
        authService.resetPassword(request);

        // Assert
        verify(passwordResetTokenRepository, times(1)).findByToken(token);
        verify(passwordEncoder, times(1)).encode(newPassword);
        verify(userRepository, times(1)).save(user);
        verify(passwordResetTokenRepository, times(1)).save(resetToken);
        assertThat(resetToken.isUsed()).isTrue();
        assertThat(user.getPassword()).isEqualTo(encodedPassword);
    }

    @Test
    @DisplayName("Should throw exception when new password is null")
    void shouldThrowExceptionWhenNewPasswordIsNull() {
        // Arrange
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("some-token");
        request.setNewPassword(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.resetPassword(request)
        );

        assertThat(exception.getMessage()).isEqualTo("A nova senha é obrigatória.");
        verify(passwordResetTokenRepository, never()).findByToken(anyString());
    }

    @Test
    @DisplayName("Should throw exception when new password is empty")
    void shouldThrowExceptionWhenNewPasswordIsEmpty() {
        // Arrange
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("some-token");
        request.setNewPassword("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.resetPassword(request)
        );

        assertThat(exception.getMessage()).isEqualTo("A nova senha é obrigatória.");
    }

    @Test
    @DisplayName("Should throw exception when reset token is invalid")
    void shouldThrowExceptionWhenResetTokenIsInvalid() {
        // Arrange
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("invalid-token");
        request.setNewPassword("newPassword123");

        when(passwordResetTokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        // Act & Assert
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.resetPassword(request)
        );

        assertThat(exception.getMessage()).isEqualTo("Link de recuperação inválido ou expirado");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when reset token is already used")
    void shouldThrowExceptionWhenResetTokenIsAlreadyUsed() {
        // Arrange
        String token = "used-token";
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken(token);
        request.setNewPassword("newPassword123");

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUsed(true);
        resetToken.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));

        // Act & Assert
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.resetPassword(request)
        );

        assertThat(exception.getMessage()).isEqualTo("Link de recuperação inválido ou expirado");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when reset token is expired")
    void shouldThrowExceptionWhenResetTokenIsExpired() {
        // Arrange
        String token = "expired-token";
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken(token);
        request.setNewPassword("newPassword123");

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUsed(false);
        resetToken.setExpiresAt(LocalDateTime.now().minusHours(1));

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));

        // Act & Assert
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.resetPassword(request)
        );

        assertThat(exception.getMessage()).isEqualTo("Link de recuperação inválido ou expirado");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when password is less than 8 characters")
    void shouldThrowExceptionWhenPasswordIsLessThan8Characters() {
        // Arrange
        String token = "valid-token";
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken(token);
        request.setNewPassword("short");

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setUsed(false);
        resetToken.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.resetPassword(request)
        );

        assertThat(exception.getMessage()).isEqualTo("A senha deve ter pelo menos 8 caracteres.");
        verify(userRepository, never()).save(any(User.class));
        verify(passwordResetTokenRepository, never()).save(resetToken);
    }
}
