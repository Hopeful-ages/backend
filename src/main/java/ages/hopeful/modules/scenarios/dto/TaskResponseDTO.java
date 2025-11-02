package ages.hopeful.modules.scenarios.dto;

import lombok.*;

import java.util.Date;
import java.util.UUID;

import ages.hopeful.modules.departments.dto.DepartmentResponseDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponseDTO {
    private UUID id;
    private String description;
    private String phase;
    private Date lastUpdateDate;
    private DepartmentResponseDTO department;

    public static TaskResponseDTO fromModel(ages.hopeful.modules.scenarios.model.Task task) {
        if (task == null) return null;
        return TaskResponseDTO.builder()
                .id(task.getId())
                .description(task.getDescription())
                .phase(task.getPhase())
                .lastUpdateDate(task.getLastUpdateDate())
                .department(DepartmentResponseDTO.fromModel(task.getDepartment()))
                .build();
    }
}
