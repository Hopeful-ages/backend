package ages.hopeful.modules.scenarios.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScenarioRequestDTO {
    private String description;
    private String origin;
    private UUID cityId;
    private UUID cobradeId;
    private List<TaskRequestDTO> tasks;
    private List<ParameterRequestDTO> parameters;
}
