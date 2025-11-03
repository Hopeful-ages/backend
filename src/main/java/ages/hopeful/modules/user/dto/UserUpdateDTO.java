package ages.hopeful.modules.user.dto;

import jakarta.annotation.Nullable;
import org.hibernate.validator.constraints.br.CPF;
import jakarta.validation.constraints.*;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    @Nullable
    @Size(max = 150)
    public String name;

    @Nullable
    @CPF
    @Pattern(regexp = "^[0-9.\\-]{11,14}$")
    public String cpf;

    @Nullable
    @Email
    @Size(max = 160)
    public String email;

    @Nullable
    @Size(max = 30)
    public String phone;

    @Nullable
    @Size(min = 8, max = 100)
    public String password;

    @Nullable
    public UUID departmentId;

    @Nullable
    public UUID cityId;
}
