package ages.hopeful.modules.cobrades.dto;

import ages.hopeful.modules.cobrades.model.Cobrade;
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
    private String subgroup;
    private String type;
    private String subType;
    private String origin;
    private String group;

    public static CobradeResponseDTO fromModel(Cobrade cobrade) {
        if (cobrade == null) return null;
        return CobradeResponseDTO.builder()
            .id(cobrade.getId())
            .code(cobrade.getCode())
            .description(cobrade.getDescription())
            .subgroup(cobrade.getSubgroup())
            .type(cobrade.getType())
            .subType(cobrade.getSubType())
            .group(cobrade.getGroup())
            .origin(cobrade.getOrigin())
            .build();
    }
}
