package ages.hopeful.modules.cobrade.dto;

import lombok.Data;

@Data
public class CobradeFilterDTO {
    private String group;
    private String subgroup;
    private String type;
    private String code;
    private Boolean active = true;
}