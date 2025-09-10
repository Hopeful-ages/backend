package ages.hopeful.modules.scenarios.service;

import ages.hopeful.modules.city.dto.CityResponseDTO;
import ages.hopeful.modules.city.model.City;
import ages.hopeful.modules.city.repository.CityRepository;
import ages.hopeful.modules.scenarios.dto.*;
import ages.hopeful.modules.scenarios.model.*;
import ages.hopeful.modules.scenarios.repository.ScenarioRepository;
import ages.hopeful.modules.scenarios.repository.CobradeRepository;
import ages.hopeful.modules.services.dto.ServiceResponseDTO;
import ages.hopeful.modules.services.model.Service;
import ages.hopeful.modules.services.repository.ServiceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ScenarioService {

    private final ScenarioRepository scenarioRepository;
    private final CityRepository cityRepository;
    private final CobradeRepository cobradeRepository;
    private final ServiceRepository serviceRepository;

    public List<ScenarioResponseDTO> getAllScenarios() {
        return scenarioRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ScenarioResponseDTO getScenarioById(UUID id) {
        Scenario scenario = scenarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Scenario não encontrado com id: " + id));
        return toResponseDTO(scenario);
    }

    public ScenarioResponseDTO createScenario(ScenarioByUserRequestDTO dto) {
        Scenario scenario = buildScenarioByUserFromDTO(dto, null);
        Scenario saved = scenarioRepository.save(scenario);
        return toResponseDTO(saved);
    }

    public ScenarioResponseDTO updateScenario(UUID id, ScenarioRequestDTO dto) {
        Scenario existing = scenarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Scenario não encontrado com id: " + id));

        Scenario scenario = buildScenarioFromDTO(dto, existing);
        scenario.setId(existing.getId()); // mantém o mesmo ID
        Scenario updated = scenarioRepository.save(scenario);

        return toResponseDTO(updated);
    }

    public void deleteScenario(UUID id) {
        if (!scenarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Scenario não encontrado com id: " + id);
        }
        scenarioRepository.deleteById(id);
    }

    // ===================== HELPERS =====================

    private Scenario buildScenarioFromDTO(ScenarioRequestDTO dto, Scenario scenario) {
        if (scenario == null) {
            scenario = new Scenario();
        }

        City city = cityRepository.findById(dto.getCityId())
                .orElseThrow(() -> new EntityNotFoundException("Cidade não encontrada"));

        Cobrade cobrade = cobradeRepository.findById(dto.getCobradeId())
                .orElseThrow(() -> new EntityNotFoundException("Cobrade não encontrado"));

        scenario.setDescription(dto.getDescription());
        scenario.setOrigin(dto.getOrigin());
        scenario.setCity(city);
        scenario.setCobrade(cobrade);

        // Tasks
        Scenario finalScenario1 = scenario;
        List<Task> tasks = dto.getTasks() != null ? dto.getTasks().stream().map(t -> {
            Service service = serviceRepository.findById(t.getServiceId())
                    .orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado"));
            Task task = new Task();
            task.setDescription(t.getDescription());
            task.setPhase(t.getFase());
            task.setLastUpdateDate(t.getLastUpdateDate());
            task.setService(service);
            task.setScenario(finalScenario1);
            return task;
        }).toList() : List.of();
        scenario.setTasks(tasks);

        // Parameters
        Scenario finalScenario = scenario;
        List<Parameter> parameters = dto.getParameters() != null ? dto.getParameters().stream().map(p -> {
            Parameter param = new Parameter();
            param.setDescription(p.getDescription());
            param.setAction(p.getAction());
            param.setFase(p.getFase());
            param.setScenario(finalScenario);
            return param;
        }).toList() : List.of();
        scenario.setParameters(parameters);

        return scenario;
    }

    private Scenario buildScenarioByUserFromDTO(ScenarioByUserRequestDTO dto, Scenario scenario) {
        if (scenario == null) {
            scenario = new Scenario();
        }

        City city = cityRepository.findById(dto.getCityId())
                .orElseThrow(() -> new EntityNotFoundException("Cidade não encontrada"));

        Cobrade cobrade = cobradeRepository.findById(dto.getCobradeId())
                .orElseThrow(() -> new EntityNotFoundException("Cobrade não encontrado"));

        scenario.setDescription(dto.getDescription());
        scenario.setOrigin(dto.getOrigin());
        scenario.setCity(city);
        scenario.setCobrade(cobrade);

        // Tasks
        Scenario finalScenario1 = scenario;
        List<Task> tasks = dto.getTasks() != null ? dto.getTasks().stream().map(t -> {
            Service service = serviceRepository.findById(t.getServiceId())
                    .orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado"));
            Task task = new Task();
            task.setDescription(t.getDescription());
            task.setPhase(t.getFase());
            task.setLastUpdateDate(t.getLastUpdateDate());
            task.setService(service);
            task.setScenario(finalScenario1);
            return task;
        }).toList() : List.of();
        scenario.setTasks(tasks);

        // Parameters
        Scenario finalScenario = scenario;
        return scenario;
    }

    private ScenarioResponseDTO toResponseDTO(Scenario scenario) {
        return ScenarioResponseDTO.builder()
                .id(scenario.getId())
                .description(scenario.getDescription())
                .origin(scenario.getOrigin())
                .city(new CityResponseDTO(
                        scenario.getCity().getId(),
                        scenario.getCity().getName(),
                        scenario.getCity().getState()
                ))
                .cobrade(new CobradeResponseDTO(
                        scenario.getCobrade().getId(),
                        scenario.getCobrade().getCode(),
                        scenario.getCobrade().getDescription(),
                        scenario.getCobrade().getGroup(),
                        scenario.getCobrade().getSubgroup(),
                        scenario.getCobrade().getType(),
                        scenario.getCobrade().getSubType()
                ))
                .tasks(scenario.getTasks().stream().map(t -> TaskResponseDTO.builder()
                        .id(t.getId())
                        .description(t.getDescription())
                        .fase(t.getPhase())
                        .lastUpdateDate(t.getLastUpdateDate())
                        .service(new ServiceResponseDTO(
                                t.getService().getId(),
                                t.getService().getName()
                        ))
                        .build()).toList())
                .parameters(scenario.getParameters().stream().map(p -> ParameterResponseDTO.builder()
                        .id(p.getId())
                        .description(p.getDescription())
                        .action(p.getAction())
                        .fase(p.getFase())
                        .build()).toList())
                .build();
    }
}
