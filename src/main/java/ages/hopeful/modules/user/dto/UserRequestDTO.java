package ages.hopeful.modules.user.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    @NotBlank
    @Size(max = 150)
    private String name;

    @NotBlank
    @Pattern(regexp = "^[0-9.\\-]{11,14}$")
    private String cpf;

    @NotBlank
    @Email
    @Size(max = 160)
    private String email;

    @Size(max = 30)
    private String phone;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    private UUID serviceId;

    private UUID cityId;
}