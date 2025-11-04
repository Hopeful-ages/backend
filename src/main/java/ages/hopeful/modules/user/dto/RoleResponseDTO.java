package ages.hopeful.modules.user.dto;

import java.util.UUID;

import ages.hopeful.modules.user.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponseDTO {
    private UUID id;
    private String name;

    public static RoleResponseDTO fromModel(Role role) {
        if (role == null) return null;
        return new RoleResponseDTO(role.getId(), role.getName());
    }
    
}
