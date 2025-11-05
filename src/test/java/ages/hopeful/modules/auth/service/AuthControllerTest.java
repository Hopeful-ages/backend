package ages.hopeful.modules.auth.service;

import ages.hopeful.modules.auth.controller.AuthController;
import ages.hopeful.modules.auth.dto.LoginRequest;
import ages.hopeful.modules.auth.dto.TokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for Authorization Controller")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("Must return 200 with token and user information when credentials are valid")
    void shouldReturn200WithTokenAndUserInfoWhenCredentialsValid() {

        LoginRequest request = new LoginRequest("john", "password123");
        TokenResponse tokenResponse = new TokenResponse("fake-jwt-token");
        when(authService.login(any(LoginRequest.class))).thenReturn(tokenResponse);

        ResponseEntity<?> response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((TokenResponse) Objects.requireNonNull(response.getBody())).getToken()).isEqualTo("fake-jwt-token");
        verify(authService, times(1)).login(any(LoginRequest.class));
    }


    @Test
    @DisplayName("Must return 400 when username is blank")
    void shouldReturn400WhenUsernameEmpty() {
        LoginRequest request = new LoginRequest("", "password123");

        ResponseEntity<?> response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Usuário e senha são obrigatórios.");
        verify(authService, never()).login(any());
    }

    @Test
    @DisplayName("Must return 400 when password is blank")
    void shouldReturn400WhenPasswordEmpty() {
        LoginRequest request = new LoginRequest("john", "");

        ResponseEntity<?> response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Usuário e senha são obrigatórios.");
        verify(authService, never()).login(any());
    }

    @Test
    @DisplayName("Must return 400 when both are blank")
    void shouldReturn400WhenBothEmpty() {
        LoginRequest request = new LoginRequest("", "");

        ResponseEntity<?> response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Usuário e senha são obrigatórios.");
        verify(authService, never()).login(any());
    }

    @Test
    @DisplayName("Must return 401 when credentials are invalid")
    void shouldReturn401WhenInvalidCredentials() {

        LoginRequest request = new LoginRequest("john", "wrongpass");
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("username or password Invalid"));

        BadCredentialsException exception =
                org.junit.jupiter.api.Assertions.assertThrows(
                        BadCredentialsException.class,
                        () -> authController.login(request)
                );

        assertThat(exception.getMessage()).isEqualTo("username or password Invalid");
        verify(authService, times(1)).login(any(LoginRequest.class));
    }

}
