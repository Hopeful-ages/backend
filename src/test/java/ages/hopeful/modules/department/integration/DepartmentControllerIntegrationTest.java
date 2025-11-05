package ages.hopeful.modules.department.integration;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import ages.hopeful.factories.DepartmentFactory;
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

    @BeforeEach
    void setup() {
        departmentRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        departmentRepository.deleteAll();
    }



    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should create a new service and persist in DB")
    void shouldCreateNewServiceAndPersist() throws Exception {
        String departmentName = "Civil Defense " + UUID.randomUUID().toString().substring(0, 5);
        DepartmentRequestDTO request = new DepartmentRequestDTO();
        request.setName(departmentName);

        mockMvc.perform(post("/api/services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(departmentName))
                .andExpect(jsonPath("$.id").isNotEmpty());

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
        Department department = departmentRepository.save(
            DepartmentFactory.createDepartment("Service to Delete " + UUID.randomUUID())
        );

        mockMvc.perform(delete("/api/services/" + department.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertFalse(
            departmentRepository.existsById(department.getId()),
            "Service should be deleted from database"
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get all services")
    void shouldGetAllServices() throws Exception {
        mockMvc.perform(get("/api/services")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

}
