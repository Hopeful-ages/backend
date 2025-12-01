package ages.hopeful.modules.scenarios.integration; 

import ages.hopeful.modules.scenarios.model.Scenario;
import ages.hopeful.modules.scenarios.repository.ScenarioRepository;
import ages.hopeful.modules.city.model.City; 
import ages.hopeful.modules.city.repository.CityRepository;
import ages.hopeful.modules.cobrades.model.Cobrade; 
import ages.hopeful.modules.cobrades.repository.CobradeRepository;
import ages.hopeful.modules.departments.repository.DepartmentRepository;
import ages.hopeful.factories.CityFactory; 
import ages.hopeful.factories.CobradeFactory;
import ages.hopeful.factories.ScenarioFactory; 
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Scenario Controller Integration Tests")
public class ScenarioControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ScenarioRepository scenarioRepository;
    @Autowired private CityRepository cityRepository;
    @Autowired private CobradeRepository cobradeRepository;
    @Autowired private DepartmentRepository departmentRepository;
    
    private UUID cityId;
    private UUID cobradeId;
    private UUID cityId2;
    private UUID cobradeId2;

    @BeforeEach
    void setup() {
        City city1 = cityRepository.save(CityFactory.createCity("Florianopolis", "SC")); 
        cityId = city1.getId();

        Cobrade cobrade1 = cobradeRepository.save(CobradeFactory.createCobrade(
            "1000", 
            "Inundacao", 
            "HIDRICO", 
            "TipoA",
            "SubtipoA"
        )); 
        cobradeId = cobrade1.getId();

        City city2 = cityRepository.save(CityFactory.createCity("Joinville", "SC"));
        cityId2 = city2.getId();
        
        Cobrade cobrade2 = cobradeRepository.save(CobradeFactory.createCobrade(
            "2000", 
            "Vendaval", 
            "METEOROLOGICO", 
            "TipoB", 
            "SubtipoB" 
        ));
        cobradeId2 = cobrade2.getId();
    }

    @AfterEach
    void tearDown() {
        scenarioRepository.deleteAll();
        cobradeRepository.deleteAll();
        cityRepository.deleteAll();
        departmentRepository.deleteAll(); 
    }

    private Scenario createPersistedScenario(UUID city, UUID cobrade, boolean published) {
        var cityEntity = cityRepository.findById(city).orElseThrow();
        var cobradeEntity = cobradeRepository.findById(cobrade).orElseThrow();
        
        Scenario scenario = ScenarioFactory.createScenario();
        scenario.setCity(cityEntity);
        scenario.setCobrade(cobradeEntity);
        scenario.setPublished(published); 
        return scenarioRepository.save(scenario);
    }

    @Nested
    @DisplayName("GET /api/scenarios/by-city-cobrade (Busca Exata)")
    class GetByCityAndCobrade {
        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("SUCESSO - Deve retornar scenario correto")
        void shouldGetByCityCobrade() throws Exception {
            Scenario testScenario = createPersistedScenario(cityId, cobradeId, true);
            
            mockMvc.perform(get("/api/scenarios/by-city-cobrade")
                            .param("cityId", cityId.toString())
                            .param("cobradeId", cobradeId.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testScenario.getId().toString()));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("FALHA - Deve retornar 404 quando não existir")
        void shouldReturn404WhenNotFound() throws Exception {
            mockMvc.perform(get("/api/scenarios/by-city-cobrade")
                            .param("cityId", cityId.toString())
                            .param("cobradeId", cobradeId2.toString()))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/scenarios/search/by-city-cobrade (Busca Flexível)")
    class SearchByCityAndCobrade {

        private final String SEARCH_URL = "/api/scenarios/search/by-city-cobrade";

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("SUCESSO - Deve retornar todos os publicados quando sem filtros")
        void shouldReturnAllPublishedWhenNoFilters() throws Exception {
            createPersistedScenario(cityId, cobradeId, true); 
            createPersistedScenario(cityId2, cobradeId2, true); 
            createPersistedScenario(cityId, cobradeId2, false); 
            
            mockMvc.perform(get(SEARCH_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("SUCESSO - Deve filtrar por cityId e ignorar não publicados")
        void shouldFilterByCityIdCorrectly() throws Exception {
            createPersistedScenario(cityId, cobradeId, true); 
            createPersistedScenario(cityId, cobradeId2, false); 
            createPersistedScenario(cityId2, cobradeId2, true); 
            
            mockMvc.perform(get(SEARCH_URL).param("cityId", cityId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        }
    }
    
    @Nested
    @DisplayName("GET /api/scenarios/pdf/{id}/scenarios-by-subgroup (Geração de PDF)")
    class GeneratePdf {
        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("SUCESSO - Deve gerar PDF, retornar application/pdf e Content-Disposition")
        void shouldGenerateValidPdf() throws Exception {
            Scenario testScenario = createPersistedScenario(cityId, cobradeId, true);
            
            mockMvc.perform(get("/api/scenarios/pdf/{id}/scenarios-by-subgroup", testScenario.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF)) 
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString(".pdf")))
                .andExpect(content().string(not(emptyOrNullString()))); 
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("FALHA - Deve retornar 404 quando scenario base não existir")
        void shouldReturnErrorWhenScenarioDoesNotExist() throws Exception {
            mockMvc.perform(get("/api/scenarios/pdf/{id}/scenarios-by-subgroup", UUID.randomUUID()))
                .andExpect(status().isNotFound()); 
        }
    }

    @Nested
    @DisplayName("PATCH /api/scenarios/{id}/changes-publish-status (Publicação)")
    class PublishScenario {
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("SUCESSO - Deve atualizar status de publicação no banco")
        void shouldPublishAndCheckDatabase() throws Exception {
            Scenario testScenario = createPersistedScenario(cityId, cobradeId, false);
            
            mockMvc.perform(patch("/api/scenarios/" + testScenario.getId() + "/changes-publish-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.published").value(true));

            Scenario updated = scenarioRepository.findById(testScenario.getId()).orElseThrow();
            assertTrue(updated.isPublished());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("FALHA - Deve retornar 404 quando scenario não existir")
        void shouldReturn404WhenScenarioDoesNotExist() throws Exception {
            mockMvc.perform(patch("/api/scenarios/" + UUID.randomUUID() + "/changes-publish-status"))
                .andExpect(status().isNotFound());
        }
    }
}