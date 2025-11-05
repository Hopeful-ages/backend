package ages.hopeful.modules.user.dto;

import ages.hopeful.modules.user.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class RoleRequestDTO {
    @NotBlank @Size(max = 150)
    private String name;

    public Role toModel() {
        Role role = new Role();
        role.setName(this.name);
        return role;
    }
}

