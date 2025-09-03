package ages.hopeful.modules.user.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserResponseDTO {

    public UUID id;
    public String name;
    public String cpf;
    public String email;
    public String phone;
    public UUID serviceId;
    public UUID cityId;
}
