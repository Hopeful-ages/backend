package ages.hopeful.users.mapper;

import ages.hopeful.users.dto.*;
import ages.hopeful.users.entity.UserEntity;

public class UserMapper {
  public static UserEntity toEntity(UserCreateDTO dto, String passwordHash) {
    UserEntity entity = new UserEntity();

    entity.setNome(dto.getNome().trim());
    entity.setCpf(dto.getCpf().replaceAll("[^0-9]", ""));
    entity.setEmail(dto.getEmail().toLowerCase());
    entity.setTelefone(dto.getTelefone());
    entity.setSenhaHash(passwordHash);
    entity.setServicoId(dto.getServicoId());
    entity.setCidadeId(dto.getCidadeId());

    return entity;
  }

  public static UserResponseDTO toResponse(UserEntity entity) {
    return UserResponseDTO.builder()
        .id(entity.getId())
        .nome(entity.getNome())
        .cpf(entity.getCpf())
        .email(entity.getEmail())
        .telefone(entity.getTelefone())
        .servicoId(entity.getServicoId())
        .cidadeId(entity.getCidadeId())
        .build();
  }

}

