package ages.hopeful.modules.services.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ages.hopeful.modules.services.model.Service;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ServiceRequestDTO {
    @NotBlank @Size(max = 150)
    private String name;

    public Service toModel() {
        Service service = new Service();
        service.setName(this.name);
        return service;
    }
}

