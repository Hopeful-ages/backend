package ages.hopeful.modules.scenarios.dto;

import ages.hopeful.modules.scenarios.model.Parameter;
import ages.hopeful.modules.scenarios.model.Scenario;
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
public class ParameterRequestDTO {
    private String description;
    private String action;
    private String fase;

    public Parameter toModel(Scenario scenario) {
        return Parameter.builder()
                .description(this.getDescription())
                .action(this.getAction())
                .phase(this.getFase())
                .scenario(scenario)
                .build();
    }
}




