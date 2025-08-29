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
  public String nome;
  public String cpf;
  public String email;
  public String telefone;
  public UUID servicoId;
  public UUID cidadeId;
}