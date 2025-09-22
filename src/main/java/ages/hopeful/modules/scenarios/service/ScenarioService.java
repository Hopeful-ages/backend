package ages.hopeful.modules.scenarios.service;

import java.util.List;
import java.util.UUID;

import ages.hopeful.modules.city.model.City;
import ages.hopeful.modules.city.repository.CityRepository;
import ages.hopeful.modules.scenarios.dto.ScenarioByUserRequestDTO;
import ages.hopeful.modules.scenarios.dto.ScenarioRequestDTO;
import ages.hopeful.modules.scenarios.dto.ScenarioResponseDTO;
import ages.hopeful.modules.scenarios.model.Cobrade;
import ages.hopeful.modules.scenarios.model.Parameter;
import ages.hopeful.modules.scenarios.model.Scenario;
import ages.hopeful.modules.scenarios.model.Task;
import ages.hopeful.modules.scenarios.repository.ScenarioRepository;
import ages.hopeful.modules.services.model.Service;
import ages.hopeful.modules.services.repository.ServiceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ScenarioService {

    private final ScenarioRepository scenarioRepository;
    private final CityRepository cityRepository;
    private final CobradeService cobradeService;
    private final ServiceRepository serviceRepository;

    public List<ScenarioResponseDTO> getAllScenarios() {
        return scenarioRepository.findAll()
                .stream()
                .map(ScenarioResponseDTO::fromModel)
                .toList();
    }

    public ScenarioResponseDTO getScenarioById(UUID id) {
        Scenario scenario = scenarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Scenario não encontrado com id: " + id));
        return ScenarioResponseDTO.fromModel(scenario);
    }

    public ScenarioResponseDTO createScenario(ScenarioByUserRequestDTO dto) {
        Scenario scenario = buildScenarioByUserFromDTO(dto, null);
        Scenario saved = scenarioRepository.save(scenario);
        return ScenarioResponseDTO.fromModel(saved);
    }

    public ScenarioResponseDTO updateScenario(UUID id, ScenarioRequestDTO dto) {
        Scenario existing = scenarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Scenario não encontrado com id: " + id));

        Scenario scenario = buildScenarioFromDTO(dto, existing);
        scenario.setId(existing.getId());
        Scenario updated = scenarioRepository.save(scenario);

        return ScenarioResponseDTO.fromModel(updated);
    }

    public void deleteScenario(UUID id) {
        if (!scenarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Scenario não encontrado com id: " + id);
        }
        scenarioRepository.deleteById(id);
    }


    private Scenario buildScenarioFromDTO(ScenarioRequestDTO dto, Scenario scenario) {
        City city = cityRepository.findById(dto.getCityId())
                .orElseThrow(() -> new EntityNotFoundException("Cidade não encontrada"));

        Cobrade cobrade = cobradeService.getCobradeById(dto.getCobradeId());

        List<Task> tasks = dto.getTasks() != null
                ? dto.getTasks().stream().map(t -> {
                    Service service = serviceRepository.findById(t.getServiceId())
                            .orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado"));
                    return t.toModel(service, scenario);
                }).toList()
                : List.of();

        List<Parameter> parameters = dto.getParameters() != null
                ? dto.getParameters().stream().map(p -> p.toModel(scenario)).toList()
                : List.of();

        if (scenario == null) {
            return dto.toModel(city, cobrade, tasks, parameters);
        }

        scenario.setOrigin(dto.getOrigin());
        scenario.setCity(city);
        scenario.setCobrade(cobrade);
        scenario.setTasks(tasks);
        scenario.setParameters(parameters);

        return scenario;
    }

    private Scenario buildScenarioByUserFromDTO(ScenarioByUserRequestDTO dto, Scenario scenario) {
        City city = cityRepository.findById(dto.getCityId())
                .orElseThrow(() -> new EntityNotFoundException("Cidade não encontrada"));

        Cobrade cobrade = cobradeService.getCobradeById(dto.getCobradeId());

        List<Task> tasks = dto.getTasks() != null
                ? dto.getTasks().stream().map(t -> {
                    Service service = serviceRepository.findById(t.getServiceId())
                            .orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado"));
                    return t.toModel(service, scenario);
                }).toList()
                : List.of();

        if (scenario == null) {
            return dto.toModel(city, cobrade, tasks);
        }

        scenario.setOrigin(dto.getOrigin());
        scenario.setCity(city);
        scenario.setCobrade(cobrade);
        scenario.setTasks(tasks);

        return scenario;
    }
}
