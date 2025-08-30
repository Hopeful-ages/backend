package ages.hopeful.modules.user.mapper;

import ages.hopeful.modules.user.dto.*;
import ages.hopeful.modules.user.model.User;

public class UserMapper {
  public static User toEntity(UserCreateDTO dto, String passwordHash) {
    User entity = new User();

    entity.setNome(dto.getNome().trim());
    entity.setCpf(dto.getCpf().replaceAll("[^0-9]", ""));
    entity.setEmail(dto.getEmail().toLowerCase());
    entity.setTelefone(dto.getTelefone());
    entity.setSenhaHash(passwordHash);
    entity.setServicoId(dto.getServicoId());
    entity.setCidadeId(dto.getCidadeId());

    return entity;
  }

  public static UserResponseDTO toResponse(User entity) {
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

