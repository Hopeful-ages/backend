package ages.hopeful.modules.user.dto;

import java.util.UUID;
import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter 
@Setter
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class UserRequestDTO {

  @NotBlank @Size(max = 150) 
  public String name;

  @NotBlank @CPF @Pattern(regexp = "^[0-9.\\-]{11,14}$")
  public String cpf;

  @NotBlank @Email @Size(max = 160)
  public String email;

  @Size(max = 30)
  public String phone;

  @NotBlank @Size(min = 6, max = 100)
  public String password;

  public UUID departmentId;

  public UUID cityId;

  public UUID roleId;
}
