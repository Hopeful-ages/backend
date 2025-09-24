package ages.hopeful.modules.cobrades.dto;

import lombok.Data;

@Data
public class CobradeFilterDTO {

    private String group;
    private String subgroup;
    private String type;
    private String code;
    private Boolean active = true;
}
