package ages.hopeful.modules.auth.service;

import ages.hopeful.config.security.jwt.JwtUtil;
import ages.hopeful.modules.auth.dto.LoginRequest;
import ages.hopeful.modules.auth.dto.TokenResponse;
import ages.hopeful.modules.user.model.Role;
import ages.hopeful.modules.user.model.User;
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
}
