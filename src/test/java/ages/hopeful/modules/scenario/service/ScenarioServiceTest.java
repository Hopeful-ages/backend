package ages.hopeful.modules.scenario.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import ages.hopeful.common.exception.NotFoundException;
import ages.hopeful.modules.scenarios.dto.ScenarioRequestDTO;
import ages.hopeful.modules.scenarios.dto.ScenarioResponseDTO;
import ages.hopeful.modules.scenarios.model.Scenario;
import ages.hopeful.modules.scenarios.repository.ScenarioRepository;
import ages.hopeful.modules.scenarios.service.*;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for ScenarioService")
class ScenarioServiceTest {

    @InjectMocks
    private ScenarioService scenarioService;

    @Mock
    private ScenarioRepository scenarioRepository;

    @Mock
    private ModelMapper modelMapper;

    private ScenarioRequestDTO scenarioRequestDTO;
    private ScenarioResponseDTO scenarioResponseDTO;
    private Scenario scenario;

    @BeforeEach
    void setUp() {
        serviceScenario = new ages.hopeful.modules.scenario.service.ScenarioServiceTest(scenarioRepository, modelMapper);
    }

    @Test
    @DisplayName("Should create a scenario successfully when data is valid")
    void shouldCreateScenarioSuccessfullyWhenDataIsValid() {
        when(modelMapper.map(any(ScenarioRequestDTO.class), eq(Scenario.class))).thenReturn(scenario);
        when(scenarioRepository.save(any(Scenario.class))).thenReturn(scenario);
        when(modelMapper.map(any(Scenario.class), eq(ScenarioResponseDTO.class))).thenReturn(scenarioResponseDTO);

        ScenarioResponseDTO response = scenarioService.createScenario(scenarioRequestDTO);

        assertNotNull(response);
        assertEquals("Test Scenario", response.getOrigin());
        verify(scenarioRepository, times(1)).save(any(Scenario.class));
    }

    @Test
    @DisplayName("Should return all scenarios")
    void shouldReturnAllScenarios() {
        when(scenarioRepository.findAll()).thenReturn(List.of(scenario));
        when(modelMapper.map(any(Scenario.class), eq(ScenarioResponseDTO.class))).thenReturn(scenarioResponseDTO);

        List<ScenarioResponseDTO> response = scenarioService.getAllScenarios();

        assertEquals(1, response.size());
        assertEquals("Test Scenario", response.get(0).getOrigin());
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
