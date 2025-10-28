package ages.hopeful.modules.scenario.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import ages.hopeful.common.exception.ConflictException;
import ages.hopeful.modules.city.model.City;
import ages.hopeful.modules.city.service.CityService;
import ages.hopeful.modules.cobrades.model.Cobrade;
import ages.hopeful.modules.cobrades.service.CobradeService;
import ages.hopeful.modules.scenarios.dto.*;
import ages.hopeful.modules.scenarios.model.Parameter;
import ages.hopeful.modules.scenarios.model.Scenario;
import ages.hopeful.modules.scenarios.model.Task;
import ages.hopeful.modules.scenarios.repository.ScenarioRepository;
import ages.hopeful.modules.services.model.Service;
import ages.hopeful.modules.services.service.ServiceService;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for Scenario Service")
class ScenarioServiceTest {

    @InjectMocks
    private ScenarioService scenarioService;

    @Mock
    private ScenarioRepository scenarioRepository;

    @Mock
    private CityService cityService;

    @Mock
    private CobradeService cobradeService;

    @Mock
    private ServiceService serviceService;

    private UUID scenarioId;
    private UUID cityId;
    private UUID cobradeId;
    private UUID serviceId;
    private City city;
    private Cobrade cobrade;
    private Service service;
    private Scenario scenario;
    private ScenarioRequestDTO scenarioRequestDTO;
    private TaskRequestDTO taskRequestDTO;
    private ParameterRequestDTO parameterRequestDTO;

    @BeforeEach
    void setUp() {
        scenarioId = UUID.randomUUID();
        cityId = UUID.randomUUID();
        cobradeId = UUID.randomUUID();
        serviceId = UUID.randomUUID();

        city = City.builder()
                .id(cityId)
                .name("Porto Alegre")
                .state("RS")
                .build();

        cobrade = Cobrade.builder()
                .id(cobradeId)
                .code("1.2.3.4.5")
                .name("Inundação")
                .category("Hidrológico")
                .subgroup("Alagamentos")
                .build();

        service = Service.builder()
                .id(serviceId)
                .name("Serviço de Emergência")
                .description("Atendimento emergencial")
                .build();

        parameterRequestDTO = ParameterRequestDTO.builder()
                .name("População Afetada")
                .value("10000")
                .build();

        taskRequestDTO = TaskRequestDTO.builder()
                .serviceId(serviceId)
                .description("Realizar evacuação")
                .priority(1)
                .build();

        scenarioRequestDTO = ScenarioRequestDTO.builder()
                .cityId(cityId)
                .cobradeId(cobradeId)
                .origin("Manual")
                .published(false)
                .tasks(List.of(taskRequestDTO))
                .parameters(List.of(parameterRequestDTO))
                .build();

        scenario = Scenario.builder()
                .id(scenarioId)
                .city(city)
                .cobrade(cobrade)
                .origin("Manual")
                .published(false)
                .tasks(new ArrayList<>())
                .parameters(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Should get all scenarios successfully")
    void shouldGetAllScenariosSuccessfully() {
        List<Scenario> scenarios = List.of(scenario);
        when(scenarioRepository.findAll()).thenReturn(scenarios);

        List<ScenarioResponseDTO> result = scenarioService.getAllScenarios();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(scenarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get scenario by id successfully")
    void shouldGetScenarioByIdSuccessfully() {
        when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.of(scenario));

        ScenarioResponseDTO result = scenarioService.getScenarioById(scenarioId);

        assertNotNull(result);
        assertEquals(scenarioId, result.getId());
        verify(scenarioRepository, times(1)).findById(scenarioId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when scenario not found by id")
    void shouldThrowEntityNotFoundExceptionWhenScenarioNotFoundById() {
        when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                scenarioService.getScenarioById(scenarioId)
        );

        verify(scenarioRepository, times(1)).findById(scenarioId);
    }

    @Test
    @DisplayName("Should get scenario by city and cobrade successfully")
    void shouldGetScenarioByCityAndCobradeSuccessfully() {
        when(scenarioRepository.findByCobradeIdAndCityId(cobradeId, cityId))
                .thenReturn(Optional.of(scenario));

        ScenarioResponseDTO result = scenarioService.getScenarioByCityAndCobrade(cityId, cobradeId);

        assertNotNull(result);
        assertEquals(scenarioId, result.getId());
        verify(scenarioRepository, times(1)).findByCobradeIdAndCityId(cobradeId, cityId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when scenario not found by city and cobrade")
    void shouldThrowEntityNotFoundExceptionWhenScenarioNotFoundByCityAndCobrade() {
        when(scenarioRepository.findByCobradeIdAndCityId(cobradeId, cityId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                scenarioService.getScenarioByCityAndCobrade(cityId, cobradeId)
        );

        verify(scenarioRepository, times(1)).findByCobradeIdAndCityId(cobradeId, cityId);
    }

    @Test
    @DisplayName("Should create scenario successfully when scenario does not exist")
    void shouldCreateScenarioSuccessfullyWhenScenarioDoesNotExist() {
        when(cityService.getCityById(cityId)).thenReturn(city);
        when(cobradeService.getCobradeEntityById(cobradeId)).thenReturn(cobrade);
        when(serviceService.getServiceById(serviceId)).thenReturn(service);
        when(scenarioRepository.findByCobradeIdAndCityId(cobradeId, cityId))
                .thenReturn(Optional.empty());
        when(scenarioRepository.save(any(Scenario.class))).thenReturn(scenario);

        ScenarioResponseDTO result = scenarioService.createScenario(scenarioRequestDTO);

        assertNotNull(result);
        verify(scenarioRepository, times(2)).save(any(Scenario.class));
        verify(cityService, times(1)).getCityById(cityId);
        verify(cobradeService, times(1)).getCobradeEntityById(cobradeId);
    }

    @Test
    @DisplayName("Should throw ConflictException when scenario already exists")
    void shouldThrowConflictExceptionWhenScenarioAlreadyExists() {
        when(cityService.getCityById(cityId)).thenReturn(city);
        when(cobradeService.getCobradeEntityById(cobradeId)).thenReturn(cobrade);
        when(scenarioRepository.findByCobradeIdAndCityId(cobradeId, cityId))
                .thenReturn(Optional.of(scenario));

        assertThrows(ConflictException.class, () ->
                scenarioService.createScenario(scenarioRequestDTO)
        );

        verify(scenarioRepository, never()).save(any(Scenario.class));
    }

    @Test
    @DisplayName("Should update scenario successfully when user is admin")
    void shouldUpdateScenarioSuccessfullyWhenUserIsAdmin() {
        when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.of(scenario));
        when(cityService.getCityById(cityId)).thenReturn(city);
        when(cobradeService.getCobradeEntityById(cobradeId)).thenReturn(cobrade);
        when(serviceService.getServiceById(serviceId)).thenReturn(service);

        ScenarioResponseDTO result = scenarioService.updateScenario(
                scenarioId, 
                scenarioRequestDTO, 
                true
        );

        assertNotNull(result);
        assertEquals(scenarioId, result.getId());
        verify(scenarioRepository, times(1)).findById(scenarioId);
        verify(cityService, times(1)).getCityById(cityId);
        verify(cobradeService, times(1)).getCobradeEntityById(cobradeId);
    }

    @Test
    @DisplayName("Should update scenario maintaining parameters when user is not admin")
    void shouldUpdateScenarioMaintainingParametersWhenUserIsNotAdmin() {
        Parameter existingParameter = Parameter.builder()
                .id(UUID.randomUUID())
                .name("Existing Parameter")
                .value("5000")
                .scenario(scenario)
                .build();
        scenario.setParameters(List.of(existingParameter));

        when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.of(scenario));
        when(cityService.getCityById(cityId)).thenReturn(city);
        when(cobradeService.getCobradeEntityById(cobradeId)).thenReturn(cobrade);
        when(serviceService.getServiceById(serviceId)).thenReturn(service);

        ScenarioResponseDTO result = scenarioService.updateScenario(
                scenarioId, 
                scenarioRequestDTO, 
                false
        );

        assertNotNull(result);
        assertEquals(1, result.getParameters().size());
        verify(scenarioRepository, times(1)).findById(scenarioId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when updating non-existent scenario")
    void shouldThrowEntityNotFoundExceptionWhenUpdatingNonExistentScenario() {
        when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                scenarioService.updateScenario(scenarioId, scenarioRequestDTO, true)
        );

        verify(scenarioRepository, times(1)).findById(scenarioId);
        verify(cityService, never()).getCityById(any());
    }

    @Test
    @DisplayName("Should delete scenario successfully")
    void shouldDeleteScenarioSuccessfully() {
        when(scenarioRepository.existsById(scenarioId)).thenReturn(true);
        doNothing().when(scenarioRepository).deleteById(scenarioId);

        scenarioService.deleteScenario(scenarioId);

        verify(scenarioRepository, times(1)).existsById(scenarioId);
        verify(scenarioRepository, times(1)).deleteById(scenarioId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when deleting non-existent scenario")
    void shouldThrowEntityNotFoundExceptionWhenDeletingNonExistentScenario() {
        when(scenarioRepository.existsById(scenarioId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
                scenarioService.deleteScenario(scenarioId)
        );

        verify(scenarioRepository, times(1)).existsById(scenarioId);
        verify(scenarioRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should get scenarios by city and cobrade search successfully")
    void shouldGetScenariosByCityAndCobradeSearchSuccessfully() {
        List<Scenario> scenarios = List.of(scenario);
        when(scenarioRepository.findByCobradeIdAndCityIdSearch(cityId, cobradeId))
                .thenReturn(scenarios);

        List<ScenarioResponseDTO> result = scenarioService.getScenarioByCityAndCobradeSearch(
                cityId, 
                cobradeId
        );

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(scenarioRepository, times(1)).findByCobradeIdAndCityIdSearch(cityId, cobradeId);
    }

    @Test
    @DisplayName("Should get related scenarios by scenario id successfully")
    void shouldGetRelatedScenariosByScenarioIdSuccessfully() {
        Scenario relatedScenario = Scenario.builder()
                .id(UUID.randomUUID())
                .city(city)
                .cobrade(cobrade)
                .origin("Automático")
                .build();

        when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.of(scenario));
        when(scenarioRepository.findScenarioGroupedBySubgroup("Alagamentos", cityId))
                .thenReturn(List.of(scenario, relatedScenario));

        List<ScenarioResponseDTO> result = scenarioService.getScenariosRelatedToScenarioById(scenarioId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(scenarioRepository, times(1)).findById(scenarioId);
        verify(scenarioRepository, times(1))
                .findScenarioGroupedBySubgroup("Alagamentos", cityId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when getting related scenarios of non-existent scenario")
    void shouldThrowEntityNotFoundExceptionWhenGettingRelatedScenariosOfNonExistentScenario() {
        when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                scenarioService.getScenariosRelatedToScenarioById(scenarioId)
        );

        verify(scenarioRepository, times(1)).findById(scenarioId);
        verify(scenarioRepository, never()).findScenarioGroupedBySubgroup(any(), any());
    }

    @Test
    @DisplayName("Should publish scenario successfully")
    void shouldPublishScenarioSuccessfully() {
        scenario.setPublished(false);
        when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.of(scenario));
        when(scenarioRepository.save(any(Scenario.class))).thenAnswer(invocation -> {
            Scenario s = invocation.getArgument(0);
            s.setPublished(true);
            return s;
        });

        ScenarioResponseDTO result = scenarioService.publishScenario(scenarioId);

        assertNotNull(result);
        assertTrue(result.isPublished());
        verify(scenarioRepository, times(1)).findById(scenarioId);
        verify(scenarioRepository, times(1)).save(any(Scenario.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when publishing non-existent scenario")
    void shouldThrowEntityNotFoundExceptionWhenPublishingNonExistentScenario() {
        when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                scenarioService.publishScenario(scenarioId)
        );

        verify(scenarioRepository, times(1)).findById(scenarioId);
        verify(scenarioRepository, never()).save(any(Scenario.class));
    }

    @Test
    @DisplayName("Should create scenario with empty tasks when tasks list is null")
    void shouldCreateScenarioWithEmptyTasksWhenTasksListIsNull() {
        scenarioRequestDTO.setTasks(null);
        
        when(cityService.getCityById(cityId)).thenReturn(city);
        when(cobradeService.getCobradeEntityById(cobradeId)).thenReturn(cobrade);
        when(scenarioRepository.findByCobradeIdAndCityId(cobradeId, cityId))
                .thenReturn(Optional.empty());
        when(scenarioRepository.save(any(Scenario.class))).thenReturn(scenario);

        ScenarioResponseDTO result = scenarioService.createScenario(scenarioRequestDTO);

        assertNotNull(result);
        verify(scenarioRepository, times(2)).save(any(Scenario.class));
    }

    @Test
    @DisplayName("Should create scenario with empty parameters when parameters list is null")
    void shouldCreateScenarioWithEmptyParametersWhenParametersListIsNull() {
        scenarioRequestDTO.setParameters(null);
        
        when(cityService.getCityById(cityId)).thenReturn(city);
        when(cobradeService.getCobradeEntityById(cobradeId)).thenReturn(cobrade);
        when(serviceService.getServiceById(serviceId)).thenReturn(service);
        when(scenarioRepository.findByCobradeIdAndCityId(cobradeId, cityId))
                .thenReturn(Optional.empty());
        when(scenarioRepository.save(any(Scenario.class))).thenReturn(scenario);

        ScenarioResponseDTO result = scenarioService.createScenario(scenarioRequestDTO);

        assertNotNull(result);
        verify(scenarioRepository, times(2)).save(any(Scenario.class));
    }
}