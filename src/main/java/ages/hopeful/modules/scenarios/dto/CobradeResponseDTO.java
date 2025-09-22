package ages.hopeful.modules.scenarios.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CobradeResponseDTO {
    private UUID id;
    private String code;
    private String description;
    private String group;
    private String subgroup;
    private String type;
    private String subType;

    public static CobradeResponseDTO fromModel(ages.hopeful.modules.scenarios.model.Cobrade cobrade) {
        if (cobrade == null) return null;
        return CobradeResponseDTO.builder()
                .id(cobrade.getId())
                .code(cobrade.getCode())
                .description(cobrade.getDescription())
                .group(cobrade.getGroup())
                .subgroup(cobrade.getSubgroup())
                .type(cobrade.getType())
                .subType(cobrade.getSubType())
                .build();
    }
}


