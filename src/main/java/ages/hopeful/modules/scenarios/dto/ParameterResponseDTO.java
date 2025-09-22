package ages.hopeful.modules.scenarios.dto;

import ages.hopeful.modules.scenarios.model.Parameter;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParameterResponseDTO {
    private UUID id;
    private String description;
    private String action;
    private String fase;

    public static ParameterResponseDTO fromModel(Parameter parameter) {
        if (parameter == null) return null;
        return ParameterResponseDTO.builder()
                .id(parameter.getId())
                .description(parameter.getDescription())
                .action(parameter.getAction())
                .fase(parameter.getPhase())
                .build();
    }
}
