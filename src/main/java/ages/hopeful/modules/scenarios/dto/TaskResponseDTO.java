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
}
