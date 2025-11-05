/*package ages.hopeful.modules.auth.integration;

import ages.hopeful.config.security.jwt.JwtUtil;
import ages.hopeful.modules.auth.dto.LoginRequest;
import ages.hopeful.modules.auth.dto.TokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Integration tests for /api/auth/login with JWT")
class AuthControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    private final String LOGIN_URL = "/api/auth/login";

    @Test
    @DisplayName("Must authenticate existing user and return valid JWT with user information")
    void shouldAuthenticateAndReturnValidJWT() throws Exception {
        LoginRequest request = new LoginRequest("abner@naoinfomado.com", "Senha");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<TokenResponse> response =
                restTemplate.postForEntity(LOGIN_URL, entity, TokenResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        String token = response.getBody().getToken();
        assertThat(token).isNotBlank();

        jwtUtil.validateToken(token);

        String username = jwtUtil.getUsernameFromToken(token);
        List<String> roles = jwtUtil.getRolesFromToken(token);
        UUID userId = jwtUtil.getUserIdFromToken(token);
        UUID cityId = jwtUtil.getCityFromToken(token);

        assertThat(username).isEqualTo("abner@naoinfomado.com");
        assertThat(roles).isNotEmpty();
        assertThat(userId).isNotNull();
        assertThat(cityId).isNotNull();
    }


    @Test
    @DisplayName("Must return 400 when username is missing")
    void shouldReturn400WhenUsernameMissing() {
        LoginRequest request = new LoginRequest("", "password123");
        HttpEntity<LoginRequest> entity = new HttpEntity<>(request);

        ResponseEntity<String> response =
                restTemplate.postForEntity(LOGIN_URL, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Usuário e senha são obrigatórios.");
    }

    @Test
    @DisplayName("Must return 401 when username does not exist")
    void shouldReturn401WhenUsernameNotExist() {
        LoginRequest request = new LoginRequest("nonexistent@example.com", "password123");
        HttpEntity<LoginRequest> entity = new HttpEntity<>(request);

        ResponseEntity<String> response =
                restTemplate.postForEntity(LOGIN_URL, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Must return 401 when password is incorrect")
    void shouldReturn401WhenPasswordIncorrect() {
        LoginRequest request = new LoginRequest("existingUser@example.com", "wrongpassword");
        HttpEntity<LoginRequest> entity = new HttpEntity<>(request);

        ResponseEntity<String> response =
                restTemplate.postForEntity(LOGIN_URL, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
*/