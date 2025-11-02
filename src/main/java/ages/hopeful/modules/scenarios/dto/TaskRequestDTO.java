package ages.hopeful.modules.scenarios.dto;

import java.util.Date;
import java.util.UUID;

import ages.hopeful.modules.scenarios.model.Scenario;
import ages.hopeful.modules.scenarios.model.Task;
import ages.hopeful.modules.services.model.Service;
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
