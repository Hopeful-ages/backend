package ages.hopeful.modules.users.dto;

import jakarta.validation.constraints.*;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    @NotBlank
    @Size(max = 150)
    public String name;

    @NotBlank
    @Pattern(regexp = "^[0-9.\\-]{11,14}$")
    public String cpf;

    @NotBlank
    @Email
    @Size(max = 160)
    public String email;

    @NotBlank
    @Size(max = 30)
    public String phone;

    @NotBlank
    @Size(min = 8, max = 100)
    public String password;

    public UUID serviceId;

    public UUID cityId;
}
