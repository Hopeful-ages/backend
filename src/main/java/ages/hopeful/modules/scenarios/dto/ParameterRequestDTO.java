package ages.hopeful.modules.scenarios.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParameterRequestDTO {
    private String description;
    private String action;
    private String fase;
}
