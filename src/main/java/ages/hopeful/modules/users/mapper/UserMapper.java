package ages.hopeful.modules.users.mapper;

import ages.hopeful.modules.users.dto.*;
import ages.hopeful.modules.users.model.User;

public class UserMapper {

    public static User toEntity(UserCreateDTO dto, String passwordHash) {
        User entity = new User();

        entity.setName(dto.getName().trim());
        entity.setCpf(dto.getCpf().replaceAll("[^0-9]", ""));
        entity.setEmail(dto.getEmail().toLowerCase());
        entity.setPhone(dto.getPhone());
        entity.setPassword(dto.getPassword());
        entity.setServiceId(dto.getServiceId());
        entity.setCityId(dto.getCityId());

        return entity;
    }

    public static UserResponseDTO toResponse(User entity) {
        return UserResponseDTO.builder()
            .id(entity.getId())
            .name(entity.getName())
            .cpf(entity.getCpf())
            .email(entity.getEmail())
            .phone(entity.getPhone())
            .serviceId(entity.getServiceId())
            .cityId(entity.getCityId())
            .build();
    }

    public static void updateEntity(User entity, UserUpdateDTO dto) {
        entity.setName(dto.getName().trim());
        entity.setCpf(dto.getCpf().replaceAll("[^0-9]", ""));
        entity.setEmail(dto.getEmail().toLowerCase());
        entity.setPassword(dto.getPassword());
        //Proteger password com hash
        entity.setPhone(dto.getPhone());
        entity.service(dto.getServiceId());
        entity.setCity(dto.getCityId());
    }
}
