package ages.hopeful.modules.users.dto;

import java.util.UUID;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UserCreateDTO {
    @NotBlank @Size(max = 150)
    public String nome;

    @NotBlank @Pattern(regexp = "^[0-9.\\-]{11,14}$")
    public String cpf;

    @NotBlank @Email @Size(max = 160)
    public String email;

    @Size(max = 30) public
    String telefone;

    @NotBlank @Size(min = 6, max = 100)
    public String senha;

    public UUID servicoId;

    public UUID cidadeId;
}
