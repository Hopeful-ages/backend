package ages.hopeful.modules.scenarios.dto;

import ages.hopeful.modules.services.dto.ServiceResponseDTO;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponseDTO {
    private UUID id;
    private String description;
    private String fase;
    private Date lastUpdateDate;
    private ServiceResponseDTO service;

    public static TaskResponseDTO fromModel(ages.hopeful.modules.scenarios.model.Task task) {
        if (task == null) return null;
        return TaskResponseDTO.builder()
                .id(task.getId())
                .description(task.getDescription())
                .fase(task.getPhase())
                .lastUpdateDate(task.getLastUpdateDate())
                .service(ServiceResponseDTO.fromModel(task.getService()))
                .build();
    }
}
