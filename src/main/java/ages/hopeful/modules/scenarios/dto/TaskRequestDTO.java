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
    private String phase;
    private Date lastUpdateDate;
    private UUID serviceId;

    public Task toModel(Service service, Scenario scenario) {
        return Task.builder()
                .description(this.getDescription())
                .phase(this.getPhase())
                .lastUpdateDate(this.lastUpdateDate != null ? this.lastUpdateDate : new Date(System.currentTimeMillis()))
                .service(service)
                .scenario(scenario)
                .build();
    }
}
