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
}
