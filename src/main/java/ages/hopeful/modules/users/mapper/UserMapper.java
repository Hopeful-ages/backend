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
        entity.setPasswordHash(passwordHash);
        entity.setServiceId(dto.getServicoId());
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

    public static void updateEntity (User entity, UserUpdateDTO userUpdateDTO) {
        entity.setName(userUpdateDTO.name.trim());
        entity.setCpf(userUpdateDTO.cpf.replaceAll("[^0-9]", ""));
        entity.setEmail(userUpdateDTO.email.toLowerCase());
        entity.setPasswordHash(encoder.encode(userUpdateDTO.password));
        entity.setPhone(userUpdateDTO.phone);
        entity.service(userUpdateDTO.service);
        entity.setCity(userUpdateDTO.city);
    }
}

