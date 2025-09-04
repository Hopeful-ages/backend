package ages.hopeful.modules.user.mapper;

import ages.hopeful.modules.user.dto.*;
import ages.hopeful.modules.user.model.User;

public class UserMapper {

    public static User toEntity(UserCreateDTO dto, String passwordHash) {
        User entity = new User();

        entity.setName(dto.getName().trim());
        entity.setCpf(dto.getCpf().replaceAll("[^0-9]", ""));
        entity.setEmail(dto.getEmail().toLowerCase());
        entity.setPhone(dto.getPhone());
        // entity.setPassword(dto.getPassword());
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
        // Validação de campos obrigatórios
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (dto.getCpf() == null || dto.getCpf().isBlank()) {
            throw new IllegalArgumentException("CPF é obrigatório");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }

        entity.setName(dto.getName().trim());
        entity.setCpf(dto.getCpf().replaceAll("[^0-9]", ""));
        entity.setEmail(dto.getEmail().toLowerCase());
        entity.setPhone(dto.getPhone());
        entity.setServiceId(dto.getServiceId());
        entity.setCityId(dto.getCityId());

        // Validação de senha
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            if (dto.getPassword().length() < 6) {
                throw new IllegalArgumentException(
                    "Senha deve ter no mínimo 6 caracteres"
                );
            }
            entity.setPassword(dto.getPassword()); // Ideal: hash aqui
        }
    }
}
