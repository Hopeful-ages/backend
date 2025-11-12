package ages.hopeful.modules.cobrades.integration;

import ages.hopeful.factories.CobradeFactory;
import ages.hopeful.modules.cobrades.model.Cobrade;
import ages.hopeful.modules.cobrades.dto.CobradeResponseDTO;
import ages.hopeful.modules.cobrades.repository.CobradeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

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

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("COBRADE Controller Integration Tests with H2")
public class CobradeIntegrationTest {

    private final String BASE_URL = "/api/cobrades";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CobradeRepository cobradeRepository;

    private Cobrade testCobrade;
    private UUID testUUID;
    private String typeParam = "Tipo Teste";
    private String subgroupParam = "Subgrupo Teste";

    @BeforeEach
    void setUp() {
        cobradeRepository.deleteAll();
        testCobrade = CobradeFactory.createInundacao();
        testCobrade.setType(typeParam);
        testCobrade.setSubgroup(subgroupParam);
        Cobrade saved = cobradeRepository.save(testCobrade);
        testUUID = saved.getId();
    }
    
    @AfterEach
    void tearDown() {
        cobradeRepository.deleteAll();
    }

    @Test
    @WithMockUser
    void getCobradeById_quandoIdValido_deveRetornarOkEPayload() throws Exception {

        mockMvc.perform(get(BASE_URL + "/{id}", testUUID) 
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.id", is(testUUID.toString())))
                .andExpect(jsonPath("$.code", is("1.2.1.0.0")))
                .andExpect(jsonPath("$.type", is(typeParam)));
    }


    @Test
    @WithMockUser
    void getAllCobrades_deveRetornarOkEListaDeCobrades() throws Exception {
        mockMvc.perform(get(BASE_URL) 
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
    
    @Test
    @WithMockUser
    void getAllCobrades_quandoListaVazia_deveRetornarOkEListaVazia() throws Exception {
        cobradeRepository.deleteAll();

        mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser
    void findAllFilter_comParametros_deveRetornarOkEListaFiltrada() throws Exception {
        mockMvc.perform(get(BASE_URL + "/filter")
                .param("type", typeParam)
                .param("subgroup", subgroupParam) 
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type", is(typeParam)))
                .andExpect(jsonPath("$[0].subgroup", is(subgroupParam)));
    }

    @Test
    @WithMockUser
    void findAllFilter_semParametros_deveRetornarOkEListaCompleta() throws Exception {
        mockMvc.perform(get(BASE_URL + "/filter")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}