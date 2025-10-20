package ages.hopeful.modules.scenarios.service;

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

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ScenarioService {

    private final ScenarioRepository scenarioRepository;
    private final CityService cityService;
    private final CobradeService cobradeService;
    private final ServiceService serviceService;

    public List<ScenarioResponseDTO> getAllScenarios() {
        return scenarioRepository
            .findAll()
            .stream()
            .map(ScenarioResponseDTO::fromModel)
            .toList();
    }

    public ScenarioResponseDTO getScenarioById(UUID id) {
        Scenario scenario = scenarioRepository
            .findById(id)
            .orElseThrow(() ->
                new EntityNotFoundException(
                    "Cenário não encontrado com id: " + id
                )
            );
        return ScenarioResponseDTO.fromModel(scenario);
    }

    public ScenarioResponseDTO getScenarioByCityAndCobrade(UUID cityId, UUID cobradeId) {
        Scenario scenario = scenarioRepository.findByCobradeIdAndCityId(cobradeId, cityId)
                .orElseThrow(() -> new EntityNotFoundException("Scenario não encontrado para cityId: " + cityId + " e cobradeId: " + cobradeId));
        return ScenarioResponseDTO.fromModel(scenario);
    }

    public ScenarioResponseDTO createScenario(ScenarioRequestDTO dto) {
        Scenario scenario = buildScenarioFromDTO(dto, null);
        if (scenarioExists(scenario)) {
            throw new ConflictException(
                "O cenário para esta cobrade já foi criado nesta cidade"
            );
        }
        Scenario newScenario = scenarioRepository.save(scenario);
        newScenario = enrichScenario(newScenario, dto);
        Scenario saved = scenarioRepository.save(newScenario);
        return ScenarioResponseDTO.fromModel(saved);
    }

    private Scenario enrichScenario(Scenario saved, ScenarioRequestDTO dto) {
        List<Task> tasks = new ArrayList<>(
            getTasksFromDTO(dto.getTasks(), saved)
        );
        List<Parameter> parameters = new ArrayList<>(
            getParametersFromDTO(dto.getParameters(), saved)
        );

        saved.setTasks(tasks);
        saved.setParameters(parameters);

        return saved;
    }

    @Transactional
    public ScenarioResponseDTO updateScenario(
            UUID id,
            ScenarioRequestDTO dto,
            boolean isAdmin
    ) {
        Scenario existing = scenarioRepository
                .findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Cenário não encontrado com id: " + id
                        )
                );
        
        Scenario scenario = buildScenarioFromDTO(dto, existing);
        scenario.getTasks().clear();
        List<Task> tasks = getTasksFromDTO(dto.getTasks(), scenario);
        for (Task t : tasks) {
            t.setScenario(scenario);
            scenario.getTasks().add(t);
        }

        if (!isAdmin) {
            scenario.setParameters(existing.getParameters());
            scenario.setPublished(existing.isPublished());
        } else {
            scenario.getParameters().clear();
            scenario.setPublished(dto.isPublished());
            List<Parameter> parameters = getParametersFromDTO(dto.getParameters(), scenario);
            for (Parameter p : parameters) {
                p.setScenario(scenario);
                scenario.getParameters().add(p);
            }
        }

        scenario.setId(existing.getId());

        return ScenarioResponseDTO.fromModel(scenario);
    }


    public void deleteScenario(UUID id) {
        if (!scenarioRepository.existsById(id)) {
            throw new EntityNotFoundException(
                "Scenario não encontrado com id: " + id
            );
        }
        scenarioRepository.deleteById(id);
    }

    private Scenario buildScenarioFromDTO(
        ScenarioRequestDTO dto,
        Scenario scenario
    ) {
        City city = cityService.getCityById(dto.getCityId());
        Cobrade cobrade = cobradeService.getCobradeEntityById(dto.getCobradeId());

        if (scenario == null) {
            return dto.toModel(city, cobrade);
        }

        scenario.setOrigin(dto.getOrigin());
        scenario.setCity(city);
        scenario.setCobrade(cobrade);

        return scenario;
    }

    private List<Task> getTasksFromDTO(
        List<TaskRequestDTO> tasks,
        Scenario scenario
    ) {
        return tasks != null
            ? tasks
                .stream()
                .map(task -> {
                    Service service = serviceService.getServiceById(
                        task.getServiceId()
                    );
                    return task.toModel(service, scenario);
                })
                .toList()
            : List.of();
    }

    private List<Parameter> getParametersFromDTO(
        List<ParameterRequestDTO> parameters,
        Scenario scenario
    ) {
        return parameters != null
            ? parameters
                .stream()
                .map(p -> p.toModel(scenario))
                .toList()
            : List.of();
    }

    private boolean scenarioExists(Scenario scenario) {
        Optional<Scenario> scenarioFromDatabase =
            scenarioRepository.findByCobradeIdAndCityId(
                scenario.getCobrade().getId(),
                scenario.getCity().getId()
            );
        if (scenarioFromDatabase.isPresent()) {
            return true;
        }
        return false;
    }

    public List<ScenarioResponseDTO> getScenarioByCityAndCobradeSearch(UUID cityId, UUID cobradeId) {
        List<Scenario> scenarios = scenarioRepository.findByCobradeIdAndCityIdSearch(cityId, cobradeId);
        return scenarios.stream().map(ScenarioResponseDTO::fromModel).toList();
    }

    public List<ScenarioResponseDTO> getScenariosRelatedToScenarioById(UUID ScenarioId) {
        Scenario scenario = scenarioRepository
            .findById(ScenarioId)
            .orElseThrow(() ->
                new EntityNotFoundException(
                    "Cenário não encontrado com id: " + ScenarioId
                )
            );
        List<Scenario> scenarios = scenarioRepository.findScenarioGroupedBySubgroup(scenario.getCobrade().getSubgroup(),
                                                                                    scenario.getCity().getId());
        return scenarios.stream().map(ScenarioResponseDTO::fromModel).toList();
    }

    //Temporário!!!
    public ScenarioResponseDTO publishScenario(UUID id) {
        Scenario scenario = scenarioRepository
            .findById(id)
            .orElseThrow(() ->
                new EntityNotFoundException(
                    "Cenário não encontrado com id: " + id
                )
            );
        scenario.setPublished(true);
        Scenario updatedScenario = scenarioRepository.save(scenario);
        return ScenarioResponseDTO.fromModel(updatedScenario);
    }
}
