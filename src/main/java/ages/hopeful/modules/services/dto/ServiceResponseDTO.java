package ages.hopeful.modules.services.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ages.hopeful.modules.services.model.Service;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponseDTO {
    private UUID id;
    private String name;

    public static ServiceResponseDTO fromModel(Service service) {
        if (service == null) return null;
        return new ServiceResponseDTO(service.getId(), service.getName());
    }
}
