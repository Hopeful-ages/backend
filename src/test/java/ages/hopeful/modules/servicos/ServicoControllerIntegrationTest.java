package ages.hopeful.modules.servicos;

import java.util.UUID;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import ages.hopeful.modules.services.dto.ServiceRequestDTO;
import ages.hopeful.modules.services.model.Service;
import ages.hopeful.modules.services.repository.ServiceRepository;
import ages.hopeful.modules.scenarios.repository.TaskRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Service Controller Integration Tests")
public class ServicoControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    @Transactional
    void setUp() {
        // Clear tasks first due to foreign key constraints
        taskRepository.deleteAll();
        serviceRepository.deleteAll();
    }

    

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should create a new service and persist in DB")
    void shouldCreateNewServiceAndPersist() throws Exception {
        // Arrange
        String serviceName = "Civil Defense " + UUID.randomUUID().toString().substring(0, 5);
        ServiceRequestDTO request = new ServiceRequestDTO();
        request.setName(serviceName);

        // Act & Assert
        mockMvc.perform(post("/api/services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(serviceName))
                .andExpect(jsonPath("$.id").isNotEmpty());

        // Verify persistence
        assertTrue(
            serviceRepository.findAll().stream()
                .anyMatch(s -> s.getName().equals(serviceName)),
            "Service should be saved in database"
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete existing service")
    void shouldDeleteExistingService() throws Exception {
        // Arrange
        Service service = new Service();
        service.setName("Service to Delete");
        service = serviceRepository.save(service);

        // Act & Assert
        mockMvc.perform(delete("/api/services/" + service.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertFalse(
            serviceRepository.existsById(service.getId()),
            "Service should be deleted from database"
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 when deleting non-existent service")
    void shouldReturn404WhenDeletingNonExistentService() throws Exception {
        mockMvc.perform(delete("/api/services/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 when creating service with empty name")
    void shouldReturn400WhenCreatingServiceWithEmptyName() throws Exception {
        // Arrange
        ServiceRequestDTO request = new ServiceRequestDTO();
        request.setName("");

        // Act & Assert
        mockMvc.perform(post("/api/services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}
