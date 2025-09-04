package ages.hopeful.modules.user.dto;

import java.util.UUID;

import ages.hopeful.modules.city.model.City;
import ages.hopeful.modules.services.model.Service;
import ages.hopeful.modules.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor // necessário para MapStruct ou ModelMapper
@AllArgsConstructor
public class UserResponseDTO {
  public UUID id;
  public String name;
  public String cpf;
  public String email;
  public String phone;
  public Service service;
  public City city;

  public static UserResponseDTO UserModelToResponse(User user){
      return UserResponseDTO.builder()
              .id(user.getId())
              .name(user.getName())
              .cpf(user.getCpf())
              .email(user.getEmail())
              .phone(user.getPhone())
              .service(user.getService())
              .city(user.getCity())
              .build();
  }
 
}