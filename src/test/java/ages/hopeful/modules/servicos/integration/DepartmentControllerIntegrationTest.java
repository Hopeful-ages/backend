package ages.hopeful.modules.servicos.integration;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import ages.hopeful.modules.departments.dto.DepartmentRequestDTO;
import ages.hopeful.modules.departments.model.Department;
import ages.hopeful.modules.departments.repository.DepartmentRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Service Controller Integration Tests")
public class DepartmentControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private DepartmentRepository departmentRepository;



    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should create a new service and persist in DB")
    void shouldCreateNewServiceAndPersist() throws Exception {
        // Arrange
        String departmentName = "Civil Defense " + UUID.randomUUID().toString().substring(0, 5);
        DepartmentRequestDTO request = new DepartmentRequestDTO();
        request.setName(departmentName);

        // Act & Assert
        mockMvc.perform(post("/api/services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(departmentName))
                .andExpect(jsonPath("$.id").isNotEmpty());

        // Verify persistence
        assertTrue(
            departmentRepository.findAll().stream()
                .anyMatch(s -> s.getName().equals(departmentName)),
            "Service should be saved in database"
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete existing service")
    void shouldDeleteExistingService() throws Exception {
        // Arrange
        Department department = new Department();
        department.setName("Service to Delete");
        department = departmentRepository.save(department);

        // Act & Assert
        mockMvc.perform(delete("/api/services/" + department.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertFalse(
            departmentRepository.existsById(department.getId()),
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
        DepartmentRequestDTO request = new DepartmentRequestDTO();
        request.setName("");

        // Act & Assert
        mockMvc.perform(post("/api/services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}
