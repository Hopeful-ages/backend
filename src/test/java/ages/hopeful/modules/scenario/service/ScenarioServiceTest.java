package ages.hopeful.modules.scenario.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import ages.hopeful.common.exception.NotFoundException;
import ages.hopeful.modules.city.model.City;
import ages.hopeful.modules.cobrades.model.Cobrade;
import ages.hopeful.modules.scenarios.dto.ParameterRequestDTO;
import ages.hopeful.modules.scenarios.dto.ScenarioRequestDTO;
import ages.hopeful.modules.scenarios.dto.ScenarioResponseDTO;
import ages.hopeful.modules.scenarios.dto.TaskRequestDTO;
import ages.hopeful.modules.scenarios.model.Scenario;
import ages.hopeful.modules.scenarios.repository.ScenarioRepository;
import ages.hopeful.modules.scenarios.service.ScenarioService;
import ages.hopeful.modules.services.model.Service;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for ScenarioService")
class ScenarioServiceTest {

    @InjectMocks
    private ScenarioService scenarioService;

    @Mock
    private ScenarioRepository scenarioRepository;

    @Mock
    private ModelMapper modelMapper;

    private UUID scenarioId;
    private UUID cityId;
    private UUID cobradeId;
    private UUID serviceId;

    private City city;
    private Cobrade cobrade;
    private Service service;

    private ParameterRequestDTO parameterRequestDTO;
    private TaskRequestDTO taskRequestDTO;
    private ScenarioRequestDTO scenarioRequestDTO;
    private ScenarioResponseDTO scenarioResponseDTO;
    private Scenario scenario;

    @BeforeEach
    void setUp() {
        scenarioId = UUID.randomUUID();
        cityId = UUID.randomUUID();
        cobradeId = UUID.randomUUID();
        serviceId = UUID.randomUUID();

        city = new City();
        city.setId(cityId);
        city.setName("Porto Alegre");
        city.setState("RS");

        cobrade = new Cobrade();
        cobrade.setId(cobradeId);
        cobrade.setCode("1.2.3.4.5");
        cobrade.setSubgroup("Inundação");
        cobrade.setType("Hidrológico");
        cobrade.setSubType("Alagamentos");

        service = new Service();
        service.setId(serviceId);
        service.setName("Serviço de Emergência");

        parameterRequestDTO = ParameterRequestDTO.builder()
                
        .description("Descrição")
                .action("Ação")
                .phase("Fase")
                .build();

        
            taskRequestDTO = TaskRequestDTO.builder()
                .description("Descrição")
                .phase("Fase")
                .lastUpdateDate(new Date(System.currentTimeMillis()))
                .serviceId(serviceId)
                .build();

        scenarioRequestDTO = ScenarioRequestDTO.builder()
                .cityId(cityId)
                .cobradeId(cobradeId)
                .origin("Manual")
                .published(false)
                .tasks(List.of(taskRequestDTO))
                .parameters(List.of(parameterRequestDTO))
                .build();

        scenario = new Scenario();
        scenario.setId(scenarioId);
        scenario.setCity(city);
        scenario.setCobrade(cobrade);
        scenario.setOrigin("Manual");
        scenario.setPublished(false);
        scenario.setTasks(new ArrayList<>());
        scenario.setParameters(new ArrayList<>());

        scenarioResponseDTO = ScenarioResponseDTO.fromModel(scenario);
    }

    @Test
    @DisplayName("Should create a scenario successfully when data is valid")
    void shouldCreateScenarioSuccessfullyWhenDataIsValid() {
        when(modelMapper.map(any(ScenarioRequestDTO.class), eq(Scenario.class))).thenReturn(scenario);
        when(scenarioRepository.save(any(Scenario.class))).thenReturn(scenario);
        when(modelMapper.map(any(Scenario.class), eq(ScenarioResponseDTO.class))).thenReturn(scenarioResponseDTO);

        ScenarioResponseDTO response = scenarioService.createScenario(scenarioRequestDTO);

        assertNotNull(response);
        assertEquals("Manual", response.getOrigin());
        verify(scenarioRepository, times(1)).save(any(Scenario.class));
    }

    @Test
    @DisplayName("Should return all scenarios")
    void shouldReturnAllScenarios() {
        when(scenarioRepository.findAll()).thenReturn(List.of(scenario));
        when(modelMapper.map(any(Scenario.class), eq(ScenarioResponseDTO.class))).thenReturn(scenarioResponseDTO);

        List<ScenarioResponseDTO> response = scenarioService.getAllScenarios();

        assertEquals(1, response.size());
        assertEquals("Manual", response.get(0).getOrigin());
        verify(scenarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should throw NotFoundException when scenario not found")
    void shouldThrowNotFoundExceptionWhenScenarioNotFound() {
        UUID invalidId = UUID.randomUUID();
        when(scenarioRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> scenarioService.getScenarioById(invalidId));
        verify(scenarioRepository, times(1)).findById(invalidId);
    }

    @Test
    @DisplayName("Should delete scenario successfully when it exists")
    void shouldDeleteScenarioSuccessfully() {
        UUID id = scenario.getId();
        when(scenarioRepository.findById(id)).thenReturn(Optional.of(scenario));

        scenarioService.deleteScenario(id);

        verify(scenarioRepository, times(1)).delete(scenario);
    }
}
