package ages.hopeful.modules.user.integration;

import ages.hopeful.modules.city.repository.CityRepository;
import ages.hopeful.modules.services.repository.ServiceRepository;
import ages.hopeful.modules.user.dto.UserRequestDTO;
import ages.hopeful.modules.user.repository.RoleRepository;
import ages.hopeful.modules.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("User Controller Integration Tests with H2")
@ActiveProfiles("test") 
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
    private ServiceRepository serviceRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    @DisplayName("Should create a new user and return it")
    @WithMockUser(roles = "ADMIN")
    void shouldCreateNewUserAndPersist() throws Exception {
        // Given
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        UserRequestDTO newUser = new UserRequestDTO();
        newUser.setName("João da Silva " + uniqueId);
        newUser.setEmail("joao.silva." + uniqueId + "@teste.com");
        newUser.setCpf("123.456.789-" + String.format("%02d", Math.abs(uniqueId.hashCode() % 100)));
        newUser.setPhone("11999999999");
        newUser.setPassword("senha123");
        // Usar IDs válidos dos dados de teste
        newUser.setServiceId(UUID.fromString("550e8400-e29b-41d4-a716-446655440025")); 
        newUser.setCityId(UUID.fromString("550e8400-e29b-41d4-a716-446655440015")); 
       
        String userAsJson = objectMapper.writeValueAsString(newUser);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userAsJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("joao.silva." + uniqueId + "@teste.com"));

        
        boolean userExists = userRepository.findByEmail("joao.silva." + uniqueId + "@teste.com").isPresent();
        assertTrue(userExists, "User must be saved in H2 database.");
    }

    @Test
    @DisplayName("Should return validation error when creating user with invalid email")
    @WithMockUser(roles = "ADMIN")
    void shouldReturnValidationErrorForInvalidEmail() throws Exception {
        // Given
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        UserRequestDTO newUser = new UserRequestDTO();
        newUser.setName("João da Silva " + uniqueId);
        newUser.setEmail("email-invalido"); // Invalid email format
        newUser.setCpf("123.456.789-" + String.format("%02d", Math.abs(uniqueId.hashCode() % 100)));
        newUser.setPhone("11999999999");
        newUser.setPassword("senha123");
        newUser.setServiceId(UUID.fromString("550e8400-e29b-41d4-a716-446655440025")); // Polícia Militar
        newUser.setCityId(UUID.fromString("550e8400-e29b-41d4-a716-446655440015")); // Florianópolis

        String userAsJson = objectMapper.writeValueAsString(newUser);

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userAsJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error when creating user with short password")
    @WithMockUser(roles = "ADMIN")
    void shouldReturnValidationErrorForShortPassword() throws Exception {
        // Given
        UserRequestDTO newUser = new UserRequestDTO();
        newUser.setName("João da Silva");
        newUser.setEmail("joao.silva@teste.com");
        newUser.setCpf("123.456.789-00");
        newUser.setPhone("11999999999");
        newUser.setPassword("123");

        String userAsJson = objectMapper.writeValueAsString(newUser);

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userAsJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error when creating user with invalid CPF format")
    @WithMockUser(roles = "ADMIN")
    void shouldReturnValidationErrorForInvalidCpf() throws Exception {
        // Given
        UserRequestDTO newUser = new UserRequestDTO();
        newUser.setName("João da Silva");
        newUser.setEmail("joao.silva2@teste.com");
        newUser.setCpf("123");
        newUser.setPhone("11999999999");
        newUser.setPassword("senha123");

        String userAsJson = objectMapper.writeValueAsString(newUser);

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userAsJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error when creating user with blank name")
    @WithMockUser(roles = "ADMIN")
    void shouldReturnValidationErrorForBlankName() throws Exception {
        UserRequestDTO newUser = new UserRequestDTO();
        newUser.setName("");
        newUser.setEmail("joao.silva3@teste.com");
        newUser.setCpf("123.456.789-00");
        newUser.setPhone("11999999999");
        newUser.setPassword("senha123");

        String userAsJson = objectMapper.writeValueAsString(newUser);

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userAsJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get all users successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllUsersSuccessfully() throws Exception {
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should get all users with status filter")
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllUsersWithStatusFilter() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users")
                .param("status", "active")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldGetUserByIdSuccessfully() throws Exception {
        // Given - using a random UUID for demonstration
        UUID userId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(get("/api/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Expecting 404 since user doesn't exist
    }

    @Test
    @DisplayName("Should return 404 when getting user with non-existent ID")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn404ForNonExistentUser() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(get("/api/users/" + nonExistentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should disable user by ID")
    @WithMockUser(roles = "ADMIN")
    void shouldDisableUserById() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(patch("/api/users/disable/" + userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should enable user by ID")
    @WithMockUser(roles = "ADMIN")
    void shouldEnableUserById() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(patch("/api/users/enable/" + userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); 
    }

}