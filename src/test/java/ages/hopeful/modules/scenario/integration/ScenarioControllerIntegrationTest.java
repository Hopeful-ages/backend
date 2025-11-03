package ages.hopeful.modules.scenario.integration;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeAll;
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

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ages.hopeful.modules.scenarios.dto.ParameterRequestDTO;
import ages.hopeful.modules.scenarios.dto.ScenarioRequestDTO;
import ages.hopeful.modules.scenarios.dto.TaskRequestDTO;
import ages.hopeful.modules.scenarios.model.Scenario;
import ages.hopeful.modules.scenarios.repository.ScenarioRepository;
import lombok.AllArgsConstructor;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Scenario Controller Integration Tests with H2")
@AllArgsConstructor
public class ScenarioControllerIntegrationTest {

    private MockMvc mockMvc;

    private ScenarioRepository scenarioRepository;

    private ObjectMapper objectMapper;

    // IDs presentes em V11__insert_data_test.sql
    private static final UUID CITY_FLORIPA = UUID.fromString("550e8400-e29b-41d4-a716-446655440015");
    private static final UUID COBRADE_INUNDACAO = UUID.fromString("550e8400-e29b-41d4-a716-446655440020");
    private static final UUID COBRADE_ENXURRADA = UUID.fromString("550e8400-e29b-41d4-a716-446655440021");
    private static final UUID EXISTING_SCENARIO_FLORIPA = UUID.fromString("550e8400-e29b-41d4-a716-446655440030");
    private static final UUID EXISTING_SCENARIO_BRASILIA = UUID.fromString("550e8400-e29b-41d4-a716-446655440031");
    private static final UUID SERVICE_OBRAS = UUID.fromString("550e8400-e29b-41d4-a716-446655440028");

    Scenario sc; 

    @BeforeAll
    void setup() {
        sc = scenarioRepository.findAll().getFirst();
    }

    private String toJson(Object o) throws Exception {
        return objectMapper.writeValueAsString(o);
    }

    private ScenarioRequestDTO buildBasicCreateRequest() {
        TaskRequestDTO task = TaskRequestDTO.builder()
                .description("Abertura de valas")
                .phase("Resposta")
                .departmentId(SERVICE_OBRAS)
                .build();

        ParameterRequestDTO param = ParameterRequestDTO.builder()
                .description("Nível da água > 1m")
                .action("Acionar alerta")
                .phase("Resposta")
                .build();

        return ScenarioRequestDTO.builder()
                .origin("Manual")
                .cityId(CITY_FLORIPA)
                .cobradeId(COBRADE_INUNDACAO)
                .tasks(List.of(task))
                .parameters(List.of(param))
                .published(false)
                .build();
    }

    @Nested
    @DisplayName("GET /api/scenarios")
    class GetScenarios {
        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should list all scenarios")
        void shouldListAll() throws Exception {
            mockMvc.perform(get("/api/scenarios"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should get one scenario by id ")
        void shouldGetById() throws Exception {
            // existente
            Scenario sc = scenarioRepository.findAll().getFirst();

            mockMvc.perform(get("/api/scenarios/" + sc.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.city").exists())
                    .andExpect(jsonPath("$.cobrade").exists());
        }
    }


    @Nested
    @DisplayName("PUT /api/scenarios/{id}")
    class UpdateScenario {
        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("User can update tasks but not parameters/published")
        void userUpdate() throws Exception {
            ScenarioRequestDTO dto = buildBasicCreateRequest();
            dto.setOrigin("Atualizado pelo USER");
            dto.setParameters(List.of());
            dto.setPublished(true); 

            mockMvc.perform(put("/api/scenarios/" + EXISTING_SCENARIO_BRASILIA)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.origin").value("Atualizado pelo USER"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Admin can update parameters and published")
        void adminUpdate() throws Exception {
            ParameterRequestDTO newParam = ParameterRequestDTO.builder()
                    .description("Novo parâmetro")
                    .action("Nova ação")
                    .phase("Resposta")
                    .build();

            ScenarioRequestDTO dto = buildBasicCreateRequest();
            dto.setOrigin("Atualizado pelo ADMIN");
            dto.setParameters(List.of(newParam));
            dto.setPublished(true);

            mockMvc.perform(put("/api/scenarios/" + sc.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.origin").value("Atualizado pelo ADMIN"))
                    .andExpect(jsonPath("$.published").value(true));
        }
    }

    @Nested
    @DisplayName("GET /api/scenarios/by-city-cobrade")
    class GetByCityAndCobrade {
        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should get scenario by city and cobrade")
        void shouldGetByCityCobrade() throws Exception {

            Scenario sc = scenarioRepository.findAll().getFirst();

            
            //mais campos
            mockMvc.perform(get("/api/scenarios/by-city-cobrade")
                            .param("cityId", sc.getCity().getId().toString())
                            .param("cobradeId", sc.getCobrade().getId().toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.cobrade").exists())
                    .andExpect(jsonPath("$.city").exists());
        }

     
    }

    @Nested
    @DisplayName("PATCH /api/scenarios/{id}/publish")
    class PublishScenario {
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should publish scenario and then appear in search")
        void shouldPublishAndSearch() throws Exception {

            mockMvc.perform(patch("/api/scenarios/" + EXISTING_SCENARIO_FLORIPA + "/publish"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.published").value(true));

            mockMvc.perform(get("/api/scenarios/search/by-city-cobrade")
                            .param("cityId", CITY_FLORIPA.toString())
                            .param("cobradeId", COBRADE_INUNDACAO.toString()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }
    }

    @Nested
    @DisplayName("DELETE /api/scenarios/{id}")
    class DeleteScenario {
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 404 when deleting non-existent scenario")
        void should404WhenDeletingNonExistent() throws Exception {
            mockMvc.perform(delete("/api/scenarios/" + UUID.randomUUID()))
                    .andExpect(status().isNotFound());
        }
    }
}
