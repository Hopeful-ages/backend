package ages.hopeful.modules.user.mapper;

import ages.hopeful.modules.user.dto.UserCreateDTO;
import ages.hopeful.modules.user.dto.UserResponseDTO;
import ages.hopeful.modules.user.model.User;

public class UserMapper {

    // Converte DTO de criação para entidade
    public static User toEntity(UserCreateDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setName(dto.getName());
        user.setCpf(dto.getCpf());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setServiceId(dto.getServiceId());
        user.setCityId(dto.getCityId());
        return user;
    }

    // Converte entidade para DTO de resposta
    public static UserResponseDTO toDto(User user) {
        if (user == null) return null;

        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .cpf(user.getCpf())
                .email(user.getEmail())
                .phone(user.getPhone())
                .serviceId(user.getServiceId())
                .cityId(user.getCityId())
                .build();
    }
}
