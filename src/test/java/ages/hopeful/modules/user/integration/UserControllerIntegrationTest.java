package ages.hopeful.modules.user.integration;

import ages.hopeful.modules.user.dto.UserRequestDTO;
import ages.hopeful.modules.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    private UserRequestDTO createValidUser(String suffix) {
        UserRequestDTO user = new UserRequestDTO();
        user.setName("João da Silva " + suffix);
        user.setEmail("joao.silva." + suffix + "@teste.com");
        user.setCpf("390.533.447-05"); // CPF válido
        user.setPhone("11999999999");
        user.setPassword("senha123");
        user.setDepartmentId(UUID.fromString("550e8400-e29b-41d4-a716-446655440005"));
        user.setCityId(UUID.fromString("550e8400-e29b-41d4-a716-446655440015"));
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
    
    //Creation of Nested test classes for organization
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

        //arrumar
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 204 when disabling non-existent user")
        void shouldReturn204WhenDisablingUser() throws Exception {
            UUID randomId = UUID.randomUUID();
            mockMvc.perform(patch("/api/users/disable/" + randomId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 204 when enabling non-existent user")
        void shouldReturn204WhenEnablingUser() throws Exception {
            UUID randomId = UUID.randomUUID();
            mockMvc.perform(patch("/api/users/enable/" + randomId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 204 when enabling existing user")
        void shouldReturn204WhenEnablingExistingUser() throws Exception {
            UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440049");

            // Primeiro desabilita
            mockMvc.perform(patch("/api/users/disable/" + userId))
                    .andExpect(status().isNoContent());

            // Agora habilita
            mockMvc.perform(patch("/api/users/enable/" + userId))
                    .andExpect(status().isNoContent());
        }
    }
}
