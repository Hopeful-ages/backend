package ages.hopeful.modules.scenarios.dto;

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
}
