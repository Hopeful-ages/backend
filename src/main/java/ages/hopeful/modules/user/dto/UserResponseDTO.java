package ages.hopeful.modules.user.dto;

import java.util.UUID;

import ages.hopeful.modules.city.dto.CityResponseDTO;
import ages.hopeful.modules.departments.model.Department;
import ages.hopeful.modules.user.model.Role;
import ages.hopeful.modules.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class UserResponseDTO {
  public UUID id;
  public String name;
  public String cpf;
  public String email;
  public String phone;
  public Department department;
  public CityResponseDTO city;
  public Boolean accountStatus;
  public Role role;

  public static UserResponseDTO UserModelToResponse(User user){
      return UserResponseDTO.builder()
              .id(user.getId())
              .name(user.getName())
              .cpf(user.getCpf())
              .email(user.getEmail())
              .phone(user.getPhone())
              .department(user.getDepartment())
              .city(CityResponseDTO.fromModel(user.getCity()))
              .accountStatus(user.getAccountStatus())
              .role(user.getRole())
              .build();
  }
 
}