package ages.hopeful.modules.cobrade.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CobradeResponseDTO {
    private String code;
    private String group;
    private String subgroup;
    private String type;
    private String subtype;
    private String description;
    private Boolean active;

    public static CobradeResponseDTO fromEntity(CobradeEntity entity) {
        return new CobradeResponseDTO(
                entity.getCode(),
                entity.getGroup(),
                entity.getSubgroup(),
                entity.getType(),
                entity.getSubtype(),
                entity.getDescription(),
                entity.getActive()
        );
    }
}