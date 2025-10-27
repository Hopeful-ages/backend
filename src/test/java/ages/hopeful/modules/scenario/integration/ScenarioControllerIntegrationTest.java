package ages.hopeful.modules.scenario.integration;

import ages.hopeful.modules.scenarios.dto.ParameterRequestDTO;
import ages.hopeful.modules.scenarios.dto.ScenarioRequestDTO;
import ages.hopeful.modules.scenarios.dto.TaskRequestDTO;
import ages.hopeful.modules.scenarios.repository.ScenarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.Test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.T.context.support.WithMockUser;
import org.springframework.T.context.ActiveProfiles;
import org.springframework.T.web.MockMvc;
import org.springframework.T.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.T.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.T.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("T")
@DisplayName("Scenario Controller Integration Tests with H2")
public class ScenarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ScenarioRepository scenarioRepository;

    // Mock valid scenario generator
    private ScenarioRequestDTO createValidScenario(String suffix) {
        ScenarioRequestDTO scenario = new ScenarioRequestDTO();
        scenario.setOrigin("Test Origin " + suffix);
        scenario.setCityId(UUID.fromString("550e8400-e29b-41d4-a716-446655440015"));
        scenario.setCobradeId(UUID.fromString("550e8400-e29b-41d4-a716-446655440025"));
        scenario.setPublished(false);
        
        // Add parameter
        ParameterRequestDTO parameter = new ParameterRequestDTO();
        parameter.setName("Test Parameter " + suffix);
        parameter.setValue("Test Value");
        List<ParameterRequestDTO> parameters = List.of(parameter);
        scenario.setParameters(parameters);
        
        return scenario;
    }
    
    // Helper to convert object to JSON string
    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
    
    // Helper to perform POST /api/scenarios
    private ResultActions postScenario(ScenarioRequestDTO scenario) throws Exception {
        return mockMvc.perform(post("/api/scenarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(scenario)));
    }
    
    // Creation of Nested T classes for organization
    @Nested
    @DisplayName("POST /api/scenarios")
    class CreateScenarioTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should create a new scenario and persist in DB")
        void shouldCreateNewScenarioAndPersist() throws Exception {
            String suffix = UUID.randomUUID().toString().substring(0, 8);
            ScenarioRequestDTO newScenario = createValidScenario(suffix);

            postScenario(newScenario)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.origin").value(newScenario.getOrigin()))
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.tasks").isArray())
                    .andExpect(jsonPath("$.parameters").isArray());

            boolean exists = scenarioRepository.findByCobradeIdAndCityId(
                    newScenario.getCobradeId(), 
                    newScenario.getCityId()
            ).isPresent();
            assertTrue(exists, "Scenario must be saved in H2 database.");
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 409 for duplicate scenario")
        void shouldReturnConflictForDuplicateScenario() throws Exception {
            String suffix = UUID.randomUUID().toString().substring(0, 8);
            ScenarioRequestDTO newScenario = createValidScenario(suffix);

            // Create first scenario
            postScenario(newScenario).andExpect(status().isOk());

            // Try to create duplicate
            postScenario(newScenario).andExpect(status().isConflict());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 400 for missing cityId")
        void shouldReturnValidationErrorForMissingCityId() throws Exception {
            ScenarioRequestDTO newScenario = createValidScenario("missingCity");
            newScenario.setCityId(null);

            postScenario(newScenario).andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 400 for missing cobradeId")
        void shouldReturnValidationErrorForMissingCobradeId() throws Exception {
            ScenarioRequestDTO newScenario = createValidScenario("missingCobrade");
            newScenario.setCobradeId(null);

            postScenario(newScenario).andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 404 for invalid cityId")
        void shouldReturnNotFoundForInvalidCityId() throws Exception {
            ScenarioRequestDTO newScenario = createValidScenario("invalidCity");
            newScenario.setCityId(UUID.randomUUID());

            postScenario(newScenario).andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/scenarios")
    class GetScenariosTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should get all scenarios")
        void shouldGetAllScenarios() throws Exception {
            mockMvc.perform(get("/api/scenarios"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should get scenario by ID")
        void shouldGetScenarioById() throws Exception {
            String suffix = UUID.randomUUID().toString().substring(0, 8);
            ScenarioRequestDTO newScenario = createValidScenario(suffix);
            
            String response = postScenario(newScenario)
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            
            UUID scenarioId = objectMapper.readTree(response).get("id").asText();
            UUID id = UUID.fromString(scenarioId);

            mockMvc.perform(get("/api/scenarios/" + id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.origin").value(newScenario.getOrigin()));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 404 when scenario not found")
        void shouldReturn404ForNonExistentScenario() throws Exception {
            UUID randomId = UUID.randomUUID();
            mockMvc.perform(get("/api/scenarios/" + randomId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should get scenario by city and cobrade")
        void shouldGetScenarioByCityAndCobrade() throws Exception {
            String suffix = UUID.randomUUID().toString().substring(0, 8);
            ScenarioRequestDTO newScenario = createValidScenario(suffix);
            
            postScenario(newScenario).andExpect(status().isOk());

            mockMvc.perform(get("/api/scenarios/by-city-cobrade")
                    .param("cityId", newScenario.getCityId().toString())
                    .param("cobradeId", newScenario.getCobradeId().toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.origin").value(newScenario.getOrigin()));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should search scenarios by city and cobrade")
        void shouldSearchScenariosByCityAndCobrade() throws Exception {
            mockMvc.perform(get("/api/scenarios/search/by-city-cobrade")
                    .param("cityId", "550e8400-e29b-41d4-a716-446655440015")
                    .param("cobradeId", "550e8400-e29b-41d4-a716-446655440025"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }
    }

    @Nested
    @DisplayName("PUT /api/scenarios")
    class UpdateScenarioTests {

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should update scenario as USER (without parameters)")
        void shouldUpdateScenarioAsUser() throws Exception {
            String suffix = UUID.randomUUID().toString().substring(0, 8);
            ScenarioRequestDTO newScenario = createValidScenario(suffix);
            
            String response = postScenario(newScenario)
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            
            UUID scenarioId = UUID.fromString(
                objectMapper.readTree(response).get("id").asText()
            );

            ScenarioRequestDTO updateScenario = createValidScenario(suffix + "-updated");
            updateScenario.setOrigin("Updated Origin");

            mockMvc.perform(put("/api/scenarios/" + scenarioId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(updateScenario)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.origin").value("Updated Origin"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should update scenario as ADMIN (with parameters)")
        void shouldUpdateScenarioAsAdmin() throws Exception {
            String suffix = UUID.randomUUID().toString().substring(0, 8);
            ScenarioRequestDTO newScenario = createValidScenario(suffix);
            
            String response = postScenario(newScenario)
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            
            UUID scenarioId = UUID.fromString(
                objectMapper.readTree(response).get("id").asText()
            );

            ScenarioRequestDTO updateScenario = createValidScenario(suffix + "-admin");
            updateScenario.setOrigin("Admin Updated Origin");
            updateScenario.setPublished(true);

            mockMvc.perform(put("/api/scenarios/" + scenarioId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(updateScenario)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.origin").value("Admin Updated Origin"))
                    .andExpect(jsonPath("$.published").value(true));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 404 when updating non-existent scenario")
        void shouldReturn404WhenUpdatingNonExistentScenario() throws Exception {
            UUID randomId = UUID.randomUUID();
            ScenarioRequestDTO updateScenario = createValidScenario("nonexistent");

            mockMvc.perform(put("/api/scenarios/" + randomId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(updateScenario)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH /api/scenarios")
    class PatchScenarioTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should publish scenario")
        void shouldPublishScenario() throws Exception {
            String suffix = UUID.randomUUID().toString().substring(0, 8);
            ScenarioRequestDTO newScenario = createValidScenario(suffix);
            
            String response = postScenario(newScenario)
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            
            UUID scenarioId = UUID.fromString(
                objectMapper.readTree(response).get("id").asText()
            );

            mockMvc.perform(patch("/api/scenarios/" + scenarioId + "/publish"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.published").value(true));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 404 when publishing non-existent scenario")
        void shouldReturn404WhenPublishingNonExistentScenario() throws Exception {
            UUID randomId = UUID.randomUUID();
            mockMvc.perform(patch("/api/scenarios/" + randomId + "/publish"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/scenarios")
    class DeleteScenarioTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should delete scenario")
        void shouldDeleteScenario() throws Exception {
            String suffix = UUID.randomUUID().toString().substring(0, 8);
            ScenarioRequestDTO newScenario = createValidScenario(suffix);
            
            String response = postScenario(newScenario)
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            
            UUID scenarioId = UUID.fromString(
                objectMapper.readTree(response).get("id").asText()
            );

            mockMvc.perform(delete("/api/scenarios/" + scenarioId))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/scenarios/" + scenarioId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 404 when deleting non-existent scenario")
        void shouldReturn404WhenDeletingNonExistentScenario() throws Exception {
            UUID randomId = UUID.randomUUID();
            mockMvc.perform(delete("/api/scenarios/" + randomId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/scenarios/pdf")
    class GetScenariosPdfTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should get scenarios by subgroup for PDF")
        void shouldGetScenariosBySubgroupForPdf() throws Exception {
            String suffix = UUID.randomUUID().toString().substring(0, 8);
            ScenarioRequestDTO newScenario = createValidScenario(suffix);
            
            String response = postScenario(newScenario)
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            
            UUID scenarioId = UUID.fromString(
                objectMapper.readTree(response).get("id").asText()
            );

            mockMvc.perform(get("/api/scenarios/pdf/" + scenarioId + "/scenarios-by-subgroup"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 404 for non-existent scenario in PDF endpoint")
        void shouldReturn404ForNonExistentScenarioInPdfEndpoint() throws Exception {
            UUID randomId = UUID.randomUUID();
            mockMvc.perform(get("/api/scenarios/pdf/" + randomId + "/scenarios-by-subgroup"))
                    .andExpect(status().isNotFound());
        }
    }
}