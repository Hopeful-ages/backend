package ages.hopeful.modules.scenarios.dto;

import ages.hopeful.modules.city.dto.CityResponseDTO;
import ages.hopeful.modules.cobrades.dto.CobradeResponseDTO;
import lombok.*;

import java.util.List;
import java.util.UUID;

import ages.hopeful.modules.scenarios.model.Scenario;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScenarioResponseDTO {
    private UUID id;
    private String description;
    private String origin;
    private CityResponseDTO city;
    private CobradeResponseDTO cobrade;
    private List<TaskResponseDTO> tasks;
    private List<ParameterResponseDTO> parameters;
    private boolean published;

    public static ScenarioResponseDTO fromModel(Scenario scenario) {
        if (scenario == null) return null;
        return ScenarioResponseDTO.builder()
                .id(scenario.getId())
                .published(scenario.isPublished())
                .origin(scenario.getOrigin())
                .city(CityResponseDTO.fromModel(scenario.getCity()))
                .cobrade(CobradeResponseDTO.fromModel(scenario.getCobrade()))
                .tasks(scenario.getTasks().stream()
                        .map(TaskResponseDTO::fromModel)
                        .toList())
                .parameters(scenario.getParameters().stream()
                        .map(ParameterResponseDTO::fromModel)
                        .toList())
                .build();
    }
}
