package ages.hopeful.modules.scenario.integration;

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

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ages.hopeful.factories.ScenarioFactory;
import ages.hopeful.factories.CityFactory;
import ages.hopeful.factories.CobradeFactory;
import ages.hopeful.factories.DepartmentFactory;
import ages.hopeful.modules.scenarios.dto.ParameterRequestDTO;
import ages.hopeful.modules.scenarios.dto.ScenarioRequestDTO;
import ages.hopeful.modules.scenarios.dto.TaskRequestDTO;
import ages.hopeful.modules.scenarios.model.Scenario;
import ages.hopeful.modules.scenarios.repository.ScenarioRepository;
import ages.hopeful.modules.city.repository.CityRepository;
import ages.hopeful.modules.cobrades.repository.CobradeRepository;
import ages.hopeful.modules.departments.repository.DepartmentRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Scenario Controller Integration Tests with H2")
public class ScenarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ScenarioRepository scenarioRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CobradeRepository cobradeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID cityId;
    private UUID cobradeId;
    private UUID departmentId;

    @BeforeEach
    void setup() {
        var city = cityRepository.save(CityFactory.createFlorianopolis());
        cityId = city.getId();

        var cobrade = cobradeRepository.save(CobradeFactory.createInundacao());
        cobradeId = cobrade.getId();

        var department = departmentRepository.save(DepartmentFactory.createObras());
        departmentId = department.getId();
    }

    @AfterEach
    void tearDown() {
        scenarioRepository.deleteAll();
        cityRepository.deleteAll();
        cobradeRepository.deleteAll();
        departmentRepository.deleteAll();
    }

    private String toJson(Object o) throws Exception {
        return objectMapper.writeValueAsString(o);
    }

    private Scenario createPersistedTestScenario() {
        var city = cityRepository.findById(cityId).orElseThrow();
        var cobrade = cobradeRepository.findById(cobradeId).orElseThrow();
        
        Scenario scenario = ScenarioFactory.createScenario();
        scenario.setCity(city);
        scenario.setCobrade(cobrade);
        return scenarioRepository.save(scenario);
    }

    private ScenarioRequestDTO buildBasicCreateRequest() {
        TaskRequestDTO task = TaskRequestDTO.builder()
                .description("Abertura de valas")
                .phase("Resposta")
                .departmentId(departmentId)
                .build();

        ParameterRequestDTO param = ParameterRequestDTO.builder()
                .description("Nível da água > 1m")
                .action("Acionar alerta")
                .phase("Resposta")
                .build();

        return ScenarioRequestDTO.builder()
                .origin("Manual")
                .cityId(cityId)
                .cobradeId(cobradeId)
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
            Scenario testScenario = createPersistedTestScenario();
            
            mockMvc.perform(get("/api/scenarios/" + testScenario.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.city").exists())
                    .andExpect(jsonPath("$.cobrade").exists());
        }
    }

    @Nested
    @DisplayName("POST /api/scenarios")
    class CreateScenario {
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should create a new scenario with tasks and parameters")
        void shouldCreateScenarioWithTasksAndParameters() throws Exception {
            ScenarioRequestDTO dto = buildBasicCreateRequest();

            mockMvc.perform(post("/api/scenarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.origin").value("Manual"))
                    .andExpect(jsonPath("$.city.id").value(cityId.toString()))
                    .andExpect(jsonPath("$.cobrade.id").value(cobradeId.toString()))
                    .andExpect(jsonPath("$.published").value(false))
                    .andExpect(jsonPath("$.tasks").isArray())
                    .andExpect(jsonPath("$.tasks[0].description").value("Abertura de valas"))
                    .andExpect(jsonPath("$.parameters").isArray())
                    .andExpect(jsonPath("$.parameters[0].description").value("Nível da água > 1m"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should create a scenario without tasks and parameters")
        void shouldCreateScenarioWithoutTasksAndParameters() throws Exception {
            ScenarioRequestDTO dto = ScenarioRequestDTO.builder()
                    .origin("Sistema de monitoramento")
                    .cityId(cityId)
                    .cobradeId(cobradeId)
                    .tasks(List.of())
                    .parameters(List.of())
                    .published(false)
                    .build();

            mockMvc.perform(post("/api/scenarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.origin").value("Sistema de monitoramento"))
                    .andExpect(jsonPath("$.tasks").isEmpty())
                    .andExpect(jsonPath("$.parameters").isEmpty());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should fail when city does not exist")
        void shouldFailWhenCityDoesNotExist() throws Exception {
            ScenarioRequestDTO dto = buildBasicCreateRequest();
            dto.setCityId(UUID.randomUUID());

            mockMvc.perform(post("/api/scenarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(dto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should fail when cobrade does not exist")
        void shouldFailWhenCobradeDoesNotExist() throws Exception {
            ScenarioRequestDTO dto = buildBasicCreateRequest();
            dto.setCobradeId(UUID.randomUUID());

            mockMvc.perform(post("/api/scenarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(dto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should create scenario with multiple tasks")
        void shouldCreateScenarioWithMultipleTasks() throws Exception {
            TaskRequestDTO task1 = TaskRequestDTO.builder()
                    .description("Evacuação de moradores")
                    .phase("Resposta")
                    .departmentId(departmentId)
                    .build();

            TaskRequestDTO task2 = TaskRequestDTO.builder()
                    .description("Monitoramento de níveis")
                    .phase("Preparação")
                    .departmentId(departmentId)
                    .build();

            TaskRequestDTO task3 = TaskRequestDTO.builder()
                    .description("Limpeza de vias")
                    .phase("Recuperação")
                    .departmentId(departmentId)
                    .build();

            ScenarioRequestDTO dto = ScenarioRequestDTO.builder()
                    .origin("Plano de emergência")
                    .cityId(cityId)
                    .cobradeId(cobradeId)
                    .tasks(List.of(task1, task2, task3))
                    .parameters(List.of())
                    .published(false)
                    .build();

            mockMvc.perform(post("/api/scenarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.tasks").isArray())
                    .andExpect(jsonPath("$.tasks.length()").value(3))
                    .andExpect(jsonPath("$.tasks[0].description").value("Evacuação de moradores"))
                    .andExpect(jsonPath("$.tasks[1].description").value("Monitoramento de níveis"))
                    .andExpect(jsonPath("$.tasks[2].description").value("Limpeza de vias"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should create scenario with multiple parameters")
        void shouldCreateScenarioWithMultipleParameters() throws Exception {
            ParameterRequestDTO param1 = ParameterRequestDTO.builder()
                    .description("Nível da água > 2m")
                    .action("Evacuação imediata")
                    .phase("Resposta")
                    .build();

            ParameterRequestDTO param2 = ParameterRequestDTO.builder()
                    .description("Precipitação > 50mm/h")
                    .action("Alerta máximo")
                    .phase("Preparação")
                    .build();

            ScenarioRequestDTO dto = ScenarioRequestDTO.builder()
                    .origin("Protocolo de emergência")
                    .cityId(cityId)
                    .cobradeId(cobradeId)
                    .tasks(List.of())
                    .parameters(List.of(param1, param2))
                    .published(false)
                    .build();

            mockMvc.perform(post("/api/scenarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.parameters").isArray())
                    .andExpect(jsonPath("$.parameters.length()").value(2))
                    .andExpect(jsonPath("$.parameters[0].action").value("Evacuação imediata"))
                    .andExpect(jsonPath("$.parameters[1].action").value("Alerta máximo"));
        }
    }

    @Nested
    @DisplayName("PUT /api/scenarios/{id}")
    class UpdateScenario {
        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("User can update tasks but not parameters/published")
        void userUpdate() throws Exception {
            Scenario testScenario = createPersistedTestScenario();
            
            ScenarioRequestDTO dto = buildBasicCreateRequest();
            dto.setOrigin("Atualizado pelo USER");
            dto.setParameters(List.of());
            dto.setPublished(true); 

            mockMvc.perform(put("/api/scenarios/" + testScenario.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.origin").value("Atualizado pelo USER"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Admin can update parameters and published")
        void adminUpdate() throws Exception {
            Scenario testScenario = createPersistedTestScenario();
            
            ParameterRequestDTO newParam = ParameterRequestDTO.builder()
                    .description("Novo parâmetro")
                    .action("Nova ação")
                    .phase("Resposta")
                    .build();

            ScenarioRequestDTO dto = buildBasicCreateRequest();
            dto.setOrigin("Atualizado pelo ADMIN");
            dto.setParameters(List.of(newParam));
            dto.setPublished(true);

            mockMvc.perform(put("/api/scenarios/" + testScenario.getId())
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
            Scenario testScenario = createPersistedTestScenario();
            
            mockMvc.perform(get("/api/scenarios/by-city-cobrade")
                            .param("cityId", cityId.toString())
                            .param("cobradeId", cobradeId.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testScenario.getId().toString()))
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
            Scenario testScenario = createPersistedTestScenario();
            
            mockMvc.perform(patch("/api/scenarios/" + testScenario.getId() + "/publish"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.published").value(true));

            mockMvc.perform(get("/api/scenarios/search/by-city-cobrade")
                            .param("cityId", cityId.toString())
                            .param("cobradeId", cobradeId.toString()))
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
