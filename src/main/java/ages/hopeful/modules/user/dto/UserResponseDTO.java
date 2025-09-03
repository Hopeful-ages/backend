package ages.hopeful.modules.user.dto;

import java.util.UUID;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
  private UUID id;
  private String name;
  private String cpf;
  private String email;
  private String phone;
  private UUID serviceId;
  private UUID cityId;
  private boolean status; // active (true) | inactive (false)
}