package ages.hopeful.modules.scenarios.dto;

import ages.hopeful.modules.city.dto.CityResponseDTO;
import lombok.*;

import java.util.List;
import java.util.UUID;

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
}
