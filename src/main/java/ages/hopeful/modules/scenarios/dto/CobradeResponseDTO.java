package ages.hopeful.modules.scenarios.dto;

import lombok.*;

import java.util.UUID;

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
}
