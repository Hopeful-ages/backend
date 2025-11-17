package ages.hopeful.modules.auth.integration;

import ages.hopeful.factories.CityFactory;
import ages.hopeful.factories.DepartmentFactory;
import ages.hopeful.factories.RoleFactory;
import ages.hopeful.factories.UserFactory;
import ages.hopeful.modules.city.repository.CityRepository;
import ages.hopeful.modules.departments.repository.DepartmentRepository;
import ages.hopeful.modules.user.model.User;
import ages.hopeful.modules.user.repository.RoleRepository;
import ages.hopeful.modules.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ages.hopeful.modules.auth.dto.LoginRequest;
import ages.hopeful.modules.auth.dto.TokenResponse;
import ages.hopeful.config.security.jwt.JwtUtil;
import ages.hopeful.modules.auth.dto.ForgotPasswordRequest;
import ages.hopeful.modules.auth.dto.ResetPasswordRequest;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Integration tests for /api/auth/login with JWT")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private final String LOGIN_URL = "/api/auth/login";
    private final String FORGOT_PASSWORD_URL = "/api/auth/forgot-password";
    private final String RESET_PASSWORD_URL = "/api/auth/reset-password";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private UUID cityId;
    private UUID departmentId;
    private UUID roleId;
    private User testUser;

    @BeforeEach
    void setup() {
        // Limpar dados anteriores
        userRepository.deleteAll();
        departmentRepository.deleteAll();
        cityRepository.deleteAll();
        roleRepository.deleteAll();
        
        var role = roleRepository.save(RoleFactory.createUserRole());
        roleId = role.getId();

        var city = cityRepository.save(CityFactory.createCity());
        cityId = city.getId();

        var department = departmentRepository.save(DepartmentFactory.createDefesaCivil());
        departmentId = department.getId();

        testUser = UserFactory.createUser("test.admin@example.com", "Test Admin", "ADMIN");
        testUser.setCity(city);
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setDepartment(department);
        testUser.setRole(role);
        testUser = userRepository.save(testUser);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should authenticate existing user and return valid JWT with user information")
    void shouldAuthenticateAndReturnValidJWT() throws Exception {
        LoginRequest request = new LoginRequest("test.admin@example.com", "password");

        // perform login
        String responseBody = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TokenResponse tokenResponse = objectMapper.readValue(responseBody, TokenResponse.class);
        String token = tokenResponse.getToken();

        jwtUtil.validateToken(token);

        String username = jwtUtil.getUsernameFromToken(token);
        assertThat(username).isEqualTo("test.admin@example.com");
        assertThat(jwtUtil.getRolesFromToken(token)).isNotEmpty();
        assertThat(jwtUtil.getUserIdFromToken(token)).isNotNull();
        assertThat(jwtUtil.getCityFromToken(token)).isNotNull();
    }

    @Test
    @DisplayName("Should authenticate existing user and return valid JWT with user information")
    void shouldReturnBadRequestForInvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest(null,null);
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

     @Test
    @DisplayName("Should authenticate existing user and return valid JWT with user information")
    void shouldReturnBadRequestForEmptyCredentials() throws Exception {
        LoginRequest request = new LoginRequest("","");
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Should send forgot password email when valid email is provided")
    void shouldSendForgotPasswordEmail() throws Exception {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("test.admin@example.com");

        mockMvc.perform(post(FORGOT_PASSWORD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Enviamos um link para redefinir sua senha"));
    }

    @Test
    @DisplayName("Should return bad request when forgot password email is missing")
    void shouldReturnBadRequestWhenForgotPasswordEmailMissing() throws Exception {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("");

        mockMvc.perform(post(FORGOT_PASSWORD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Email é obrigatório"));
    }

    @Test
    @DisplayName("Should reset password when valid token and password are provided")
    void shouldResetPasswordSuccessfully() throws Exception {
        // Simula geração de token de recuperação (em produção seria enviado por email)
        String resetToken = "dummy-reset-token";
        // Simula que o AuthService aceitará esse token (mock ou ajuste necessário no serviço real/teste)
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken(resetToken);
        request.setNewPassword("newStrongPassword");

        // Dependendo da implementação, talvez precise mockar o AuthService para aceitar esse token.
        // Aqui esperamos apenas o fluxo HTTP.
        mockMvc.perform(post(RESET_PASSWORD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Senha alterada com sucesso"));
    }

    @Test
    @DisplayName("Should return bad request when reset password is too short")
    void shouldReturnBadRequestWhenResetPasswordTooShort() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("dummy-reset-token");
        request.setNewPassword("short");

        mockMvc.perform(post(RESET_PASSWORD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Senha deve ter no mínimo 8 caracteres"));
    }

}
