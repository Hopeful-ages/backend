package ages.hopeful.modules.scenario.integration;
import ages.hopeful.modules.scenarios.dto.ParameterRequestDTO;
import ages.hopeful.modules.scenarios.dto.ScenarioRequestDTO;
import ages.hopeful.modules.scenarios.model.Scenario;
import ages.hopeful.modules.scenarios.repository.ScenarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;
import java.util.UUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.;
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Scenario Controller Integration Tests")
class ScenarioControllerIntegrationTest {
@Autowired
private MockMvc mockMvc;

@Autowired
private ScenarioRepository scenarioRepository;

@Autowired
private ObjectMapper objectMapper;

@BeforeEach
void setUp() {
    scenarioRepository.deleteAll();
}

private ScenarioRequestDTO createValidScenarioRequest() {
    ParameterRequestDTO parameter = ParameterRequestDTO.builder()
            .name("População")
            .value("10000")
            .build();

    return ScenarioRequestDTO.builder()
            .name("Cenário de Teste")
            .description("Descrição do cenário")
            .parameters(List.of(parameter))
            .build();
}

private ResultActions performPost(String url, Object content) throws Exception {
    return mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(content)));
}

private ResultActions performGet(String url) throws Exception {
    return mockMvc.perform(get(url)
            .contentType(MediaType.APPLICATION_JSON));
}

private ResultActions performPut(String url, Object content) throws Exception {
    return mockMvc.perform(put(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(content)));
}

private ResultActions performPatch(String url, Object content) throws Exception {
    return mockMvc.perform(patch(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(content)));
}

private ResultActions performDelete(String url) throws Exception {
    return mockMvc.perform(delete(url)
            .contentType(MediaType.APPLICATION_JSON));
}

@Nested
@DisplayName("Create Scenario Tests")
class CreateScenarioTests {

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create scenario successfully with valid data")
    void shouldCreateScenarioSuccessfully() throws Exception {
        ScenarioRequestDTO requestDTO = createValidScenarioRequest();

        performPost("/api/scenarios", requestDTO)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Cenário de Teste"))
                .andExpect(jsonPath("$.description").value("Descrição do cenário"))
                .andExpect(jsonPath("$.parameters").isArray())
                .andExpect(jsonPath("$.parameters[0].name").value("População"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 403 when user without admin role tries to create")
    void shouldReturn403WhenUserWithoutAdminRole() throws Exception {
        ScenarioRequestDTO requestDTO = createValidScenarioRequest();

        performPost("/api/scenarios", requestDTO)
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 when name is null")
    void shouldReturn400WhenNameIsNull() throws Exception {
        ScenarioRequestDTO requestDTO = createValidScenarioRequest();
        requestDTO.setName(null);

        performPost("/api/scenarios", requestDTO)
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 when name is empty")
    void shouldReturn400WhenNameIsEmpty() throws Exception {
        ScenarioRequestDTO requestDTO = createValidScenarioRequest();
        requestDTO.setName("");

        performPost("/api/scenarios", requestDTO)
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 when parameters is null")
    void shouldReturn400WhenParametersIsNull() throws Exception {
        ScenarioRequestDTO requestDTO = createValidScenarioRequest();
        requestDTO.setParameters(null);

        performPost("/api/scenarios", requestDTO)
                .andExpect(status().isBadRequest());
    }
}

@Nested
@DisplayName("Get Scenarios Tests")
class GetScenariosTests {

    @Test
    @WithMockUser
    @DisplayName("Should return all scenarios")
    void shouldReturnAllScenarios() throws Exception {
        performGet("/api/scenarios")
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return empty array when no scenarios exist")
    void shouldReturnEmptyArrayWhenNoScenarios() throws Exception {
        performGet("/api/scenarios")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return scenario by id")
    void shouldReturnScenarioById() throws Exception {
        Scenario scenario = scenarioRepository.save(Scenario.builder()
                .id(UUID.randomUUID())
                .name("Cenário Teste")
                .description("Descrição")
                .build());

        performGet("/api/scenarios/" + scenario.getId().toString())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cenário Teste"))
                .andExpect(jsonPath("$.description").value("Descrição"));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 404 when scenario not found")
    void shouldReturn404WhenScenarioNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();

        performGet("/api/scenarios/" + randomId.toString())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 when id format is invalid")
    void shouldReturn400WhenIdFormatIsInvalid() throws Exception {
        performGet("/api/scenarios/invalid-uuid")
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Should filter scenarios by name")
    void shouldFilterScenariosByName() throws Exception {
        scenarioRepository.save(Scenario.builder()
                .id(UUID.randomUUID())
                .name("Cenário A")
                .build());

        performGet("/api/scenarios?name=Cenário A")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Cenário A"));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return scenarios with pagination")
    void shouldReturnScenariosWithPagination() throws Exception {
        performGet("/api/scenarios?page=0&size=10")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}

@Nested
@DisplayName("Update Scenario Tests")
class UpdateScenarioTests {

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update scenario successfully")
    void shouldUpdateScenarioSuccessfully() throws Exception {
        Scenario scenario = scenarioRepository.save(Scenario.builder()
                .id(UUID.randomUUID())
                .name("Nome Original")
                .description("Descrição Original")
                .build());

        ScenarioRequestDTO updateDTO = createValidScenarioRequest();
        updateDTO.setName("Nome Atualizado");

        performPut("/api/scenarios/" + scenario.getId().toString(), updateDTO)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nome Atualizado"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 when updating non-existent scenario")
    void shouldReturn404WhenUpdatingNonExistentScenario() throws Exception {
        UUID randomId = UUID.randomUUID();
        ScenarioRequestDTO updateDTO = createValidScenarioRequest();

        performPut("/api/scenarios/" + randomId.toString(), updateDTO)
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update only provided fields")
    void shouldUpdateOnlyProvidedFields() throws Exception {
        Scenario scenario = scenarioRepository.save(Scenario.builder()
                .id(UUID.randomUUID())
                .name("Nome Original")
                .description("Descrição Original")
                .build());

        ScenarioRequestDTO updateDTO = ScenarioRequestDTO.builder()
                .name("Nome Atualizado")
                .build();

        performPut("/api/scenarios/" + scenario.getId().toString(), updateDTO)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nome Atualizado"))
                .andExpect(jsonPath("$.description").value("Descrição Original"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 403 when user without admin role tries to update")
    void shouldReturn403WhenUserWithoutAdminRoleTries() throws Exception {
        UUID randomId = UUID.randomUUID();
        ScenarioRequestDTO updateDTO = createValidScenarioRequest();

        performPut("/api/scenarios/" + randomId.toString(), updateDTO)
                .andExpect(status().isForbidden());
    }
}

@Nested
@DisplayName("Patch Scenario Tests")
class PatchScenarioTests {

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should patch scenario successfully")
    void shouldPatchScenarioSuccessfully() throws Exception {
        Scenario scenario = scenarioRepository.save(Scenario.builder()
                .id(UUID.randomUUID())
                .name("Nome Original")
                .description("Descrição Original")
                .build());

        ScenarioRequestDTO patchDTO = ScenarioRequestDTO.builder()
                .name("Nome Modificado")
                .build();

        performPatch("/api/scenarios/" + scenario.getId().toString(), patchDTO)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nome Modificado"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 when patching non-existent scenario")
    void shouldReturn404WhenPatchingNonExistentScenario() throws Exception {
        UUID randomId = UUID.randomUUID();
        ScenarioRequestDTO patchDTO = createValidScenarioRequest();

        performPatch("/api/scenarios/" + randomId.toString(), patchDTO)
                .andExpect(status().isNotFound());
    }
}

@Nested
@DisplayName("Delete Scenario Tests")
class DeleteScenarioTests {

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete scenario successfully")
    void shouldDeleteScenarioSuccessfully() throws Exception {
        Scenario scenario = scenarioRepository.save(Scenario.builder()
                .id(UUID.randomUUID())
                .name("Cenário para Deletar")
                .build());

        performDelete("/api/scenarios/" + scenario.getId().toString())
                .andExpect(status().isNoContent());

        // Verifica se foi realmente deletado
        performGet("/api/scenarios/" + scenario.getId().toString())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 when deleting non-existent scenario")
    void shouldReturn404WhenDeletingNonExistentScenario() throws Exception {
        UUID randomId = UUID.randomUUID();

        performDelete("/api/scenarios/" + randomId.toString())
                .andExpect(status().isNotFound());
    }
}

@Nested
@DisplayName("Get Scenarios PDF Tests")
class GetScenariosPdfTests {

    @Test
    @WithMockUser
    @DisplayName("Should generate PDF successfully")
    void shouldGeneratePdfSuccessfully() throws Exception {
        scenarioRepository.save(Scenario.builder()
                .id(UUID.randomUUID())
                .name("Cenário PDF")
                .description("Para teste de PDF")
                .build());

        performGet("/api/scenarios/pdf")
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    @WithMockUser
    @DisplayName("Should generate empty PDF when no scenarios exist")
    void shouldGenerateEmptyPdfWhenNoScenarios() throws Exception {
        performGet("/api/scenarios/pdf")
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }
}
}