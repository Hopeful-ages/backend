package ages.hopeful.modules.scenarios.dto;

import ages.hopeful.modules.scenarios.model.Scenario;
import ages.hopeful.modules.services.model.Service;
import ages.hopeful.modules.scenarios.model.Task;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequestDTO {
    private String description;
    private String fase;
    private Date lastUpdateDate;
    private UUID serviceId;

    public Task toModel(Service service, Scenario scenario) {
        return Task.builder()
                .description(this.getDescription())
                .phase(this.getFase())
                .lastUpdateDate(this.getLastUpdateDate())
                .service(service)
                .scenario(scenario)
                .build();
    }
}
