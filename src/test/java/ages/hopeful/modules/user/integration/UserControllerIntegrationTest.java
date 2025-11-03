package ages.hopeful.modules.user.integration;

import ages.hopeful.factories.UserFactory;
import ages.hopeful.factories.RoleFactory;
import ages.hopeful.factories.CityFactory;
import ages.hopeful.factories.DepartmentFactory;
import ages.hopeful.modules.user.dto.UserRequestDTO;
import ages.hopeful.modules.user.model.User;
import ages.hopeful.modules.user.repository.UserRepository;
import ages.hopeful.modules.user.repository.RoleRepository;
import ages.hopeful.modules.city.repository.CityRepository;
import ages.hopeful.modules.departments.repository.DepartmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("User Controller Integration Tests with H2")
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private UUID cityId;
    private UUID departmentId;
    private User testUser;

    @BeforeEach
    void setup() {
        var role = roleRepository.save(RoleFactory.createUserRole());

        var city = cityRepository.save(CityFactory.createFlorianopolis());
        cityId = city.getId();

        var department = departmentRepository.save(DepartmentFactory.createDefesaCivil());
        departmentId = department.getId();

        testUser = UserFactory.createUser("test.user@example.com", "Test User", "USER");
        testUser.setCity(city);
        testUser.setDepartment(department);
        testUser.setRole(role);
        testUser = userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        cityRepository.deleteAll();
        departmentRepository.deleteAll();
        roleRepository.deleteAll();
    }

    private UserRequestDTO createValidUser(String suffix) {
        UserRequestDTO user = new UserRequestDTO();
        user.setName("João da Silva " + suffix);
        user.setEmail("joao.silva." + suffix + "@teste.com");
        user.setCpf("390.533.447-05");
        user.setPhone("11999999999");
        user.setPassword("senha123");
        user.setDepartmentId(departmentId);
        user.setCityId(cityId);
        return user;
    }
    
    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
    private ResultActions postUser(UserRequestDTO user) throws Exception {
        return mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(user)));
    }
    
    @Nested
    @DisplayName("POST /api/users")
    class CreateUserTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should create a new user and persist in DB")
        void shouldCreateNewUserAndPersist() throws Exception {
            String suffix = UUID.randomUUID().toString().substring(0, 8);
            UserRequestDTO newUser = createValidUser(suffix);

            postUser(newUser)
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.email").value(newUser.getEmail()))
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.phone").value(newUser.getPhone()))
                    .andExpect(jsonPath("$.name").value(newUser.getName()));

            boolean exists = userRepository.findByEmail(newUser.getEmail()).isPresent();
            assertTrue(exists, "User must be saved in H2 database.");
        }
    }

    @Nested
    @DisplayName("GET /api/users")
    class GetUsersTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should get all users")
        void shouldGetAllUsers() throws Exception {
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should get all users with status filter")
        void shouldGetAllUsersWithStatusFilter() throws Exception {
            mockMvc.perform(get("/api/users").param("status", "active"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

      
    }

    @Nested
    @DisplayName("PATCH /api/users")
    class UpdateUserTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 404 when disabling non-existent user")
        void shouldReturn404WhenDisablingUser() throws Exception {
            UUID randomId = UUID.randomUUID();
            mockMvc.perform(patch("/api/users/disable/" + randomId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 404 when enabling non-existent user")
        void shouldReturn404WhenEnablingUser() throws Exception {
            UUID randomId = UUID.randomUUID();
            mockMvc.perform(patch("/api/users/enable/" + randomId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should disable and then enable existing user")
        void shouldDisableAndEnableExistingUser() throws Exception {
            mockMvc.perform(patch("/api/users/disable/" + testUser.getId()))
                    .andExpect(status().isNoContent());

            User disabled = userRepository.findById(testUser.getId()).orElseThrow();
            assertFalse(disabled.getAccountStatus(), "User should be disabled");

            mockMvc.perform(patch("/api/users/enable/" + testUser.getId()))
                    .andExpect(status().isNoContent());

            User enabled = userRepository.findById(testUser.getId()).orElseThrow();
            assertTrue(enabled.getAccountStatus(), "User should be enabled");
        }
    }
}
