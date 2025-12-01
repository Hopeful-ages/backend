package ages.hopeful.modules.scenarios.service;

import ages.hopeful.modules.scenarios.dto.ScenarioResponseDTO;
import ages.hopeful.modules.scenarios.model.Scenario;
import ages.hopeful.modules.scenarios.repository.ScenarioRepository;
import ages.hopeful.modules.cobrades.model.Cobrade;
import ages.hopeful.modules.city.model.City;
import ages.hopeful.modules.city.service.CityService;
import ages.hopeful.modules.cobrades.service.CobradeService;
import ages.hopeful.modules.departments.service.DepartmentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Scenario Service Unit Tests")
public class ScenarioServiceTest {

    @InjectMocks
    private ScenarioService scenarioService;

    @Mock
    private ScenarioRepository scenarioRepository;
    @Mock
    private CityService cityService;
    @Mock
    private CobradeService cobradeService;
    @Mock
    private DepartmentService departmentService;

    private final UUID SCENARIO_ID = UUID.randomUUID();
    private final UUID CITY_ID = UUID.randomUUID();
    private final UUID COBRADE_ID = UUID.randomUUID();
    private Scenario mockScenario;
    private Cobrade mockCobrade;
    private City mockCity;

    @BeforeEach
    void setup() {
        mockCity = new City();
        mockCity.setId(CITY_ID);
        
        mockCobrade = new Cobrade();
        mockCobrade.setId(COBRADE_ID);
        mockCobrade.setSubgroup("HIDRICO");

        mockScenario = Scenario.builder()
                .id(SCENARIO_ID)
                .city(mockCity)
                .cobrade(mockCobrade)
                .published(false)
                .build();
    }

    @Test
    @DisplayName("SUCESSO - Busca Exata: Deve retornar scenario por cityId e cobradeId")
    void getByCityCobrade_ShouldReturnScenario() {
        when(scenarioRepository.findByCobradeIdAndCityId(eq(COBRADE_ID), eq(CITY_ID)))
                .thenReturn(Optional.of(mockScenario));

        ScenarioResponseDTO result = scenarioService.getScenarioByCityAndCobrade(CITY_ID, COBRADE_ID);

        assertNotNull(result);
        assertEquals(SCENARIO_ID, result.getId());
    }

    @Test
    @DisplayName("FALHA - Busca Exata: Deve lançar EntityNotFoundException quando scenario não existir")
    void getByCityCobrade_ShouldThrowExceptionWhenNotFound() {
        when(scenarioRepository.findByCobradeIdAndCityId(any(), any()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            scenarioService.getScenarioByCityAndCobrade(CITY_ID, COBRADE_ID);
        });
    }

    @Test
    @DisplayName("SUCESSO - Busca Flexível: Deve retornar lista quando passar apenas cityId")
    void searchByCityCobrade_ShouldReturnListWhenOnlyCityId() {
        when(scenarioRepository.findByCobradeIdAndCityIdSearch(eq(CITY_ID), eq(null)))
                .thenReturn(List.of(mockScenario));

        List<ScenarioResponseDTO> result = scenarioService.getScenarioByCityAndCobradeSearch(CITY_ID, null);

        assertFalse(result.isEmpty());
        verify(scenarioRepository, times(1)).findByCobradeIdAndCityIdSearch(CITY_ID, null);
    }
    
    @Test
    @DisplayName("SUCESSO - Busca Flexível: Deve retornar lista quando não passar nenhum filtro")
    void searchByCityCobrade_ShouldReturnListWhenNonePassed() {
        when(scenarioRepository.findByCobradeIdAndCityIdSearch(eq(null), eq(null)))
                .thenReturn(List.of(mockScenario));

        List<ScenarioResponseDTO> result = scenarioService.getScenarioByCityAndCobradeSearch(null, null);

        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("SUCESSO - Publicação: Deve publicar scenario (false -> true)")
    void changePublishStatus_ShouldPublishScenario() {
        mockScenario.setPublished(false);
        when(scenarioRepository.findById(SCENARIO_ID)).thenReturn(Optional.of(mockScenario));
        when(scenarioRepository.save(any(Scenario.class))).thenAnswer(i -> i.getArguments()[0]);

        ScenarioResponseDTO result = scenarioService.changePublishStatus(SCENARIO_ID);

        assertTrue(result.isPublished());
        assertTrue(mockScenario.isPublished()); 
        verify(scenarioRepository, times(1)).save(mockScenario);
    }

    @Test
    @DisplayName("SUCESSO - Publicação: Deve despublicar scenario (true -> false)")
    void changePublishStatus_ShouldUnpublishScenario() {
        mockScenario.setPublished(true);
        when(scenarioRepository.findById(SCENARIO_ID)).thenReturn(Optional.of(mockScenario));
        when(scenarioRepository.save(any(Scenario.class))).thenAnswer(i -> i.getArguments()[0]);

        ScenarioResponseDTO result = scenarioService.changePublishStatus(SCENARIO_ID);

        assertFalse(result.isPublished());
        assertFalse(mockScenario.isPublished());
    }

    @Test
    @DisplayName("FALHA - Publicação: Deve lançar EntityNotFoundException ao publicar scenario inexistente")
    void changePublishStatus_ShouldThrowExceptionWhenNotFound() {
        when(scenarioRepository.findById(SCENARIO_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            scenarioService.changePublishStatus(SCENARIO_ID);
        });
        verify(scenarioRepository, never()).save(any(Scenario.class)); 
    }

    @Test
    @DisplayName("SUCESSO - PDF/Relacionados: Deve retornar scenarios relacionados por Subgrupo e cityId")
    void getScenariosRelated_ShouldReturnList() {
        when(scenarioRepository.findById(SCENARIO_ID)).thenReturn(Optional.of(mockScenario)); 
        when(scenarioRepository.findScenarioGroupedBySubgroup(eq("HIDRICO"), eq(CITY_ID))) 
                .thenReturn(List.of(mockScenario));

        List<ScenarioResponseDTO> result = scenarioService.getScenariosRelatedToScenarioById(SCENARIO_ID);

        assertFalse(result.isEmpty());
        verify(scenarioRepository, times(1)).findScenarioGroupedBySubgroup("HIDRICO", CITY_ID); 
    }
    
    @Test
    @DisplayName("FALHA - PDF/Relacionados: Deve lançar EntityNotFoundException quando o cenário base não existir")
    void getScenariosRelated_ShouldThrowExceptionWhenBaseScenarioNotFound() {
        when(scenarioRepository.findById(SCENARIO_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            scenarioService.getScenariosRelatedToScenarioById(SCENARIO_ID);
        });
    }
}