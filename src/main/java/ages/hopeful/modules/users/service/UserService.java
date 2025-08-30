package ages.hopeful.modules.users.service;

import ages.hopeful.exception.ConflictException;
import ages.hopeful.modules.users.dto.*;
import ages.hopeful.modules.users.mapper.UserMapper;
import ages.hopeful.modules.users.repository.UserRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
  private final UserRepository repo; private final BCryptPasswordEncoder encoder;

  public UserService(UserRepository repo, BCryptPasswordEncoder encoder) { 
    this.repo = repo; 
    this.encoder = encoder; 
  }

  @Transactional
  public UserResponseDTO save(UserCreateDTO dto) {
    var lowerCaseEmail = dto.getEmail().toLowerCase();
    var cpfWithoutMask = dto.getCpf().replaceAll("[^0-9]", "");

    if (repo.existsByEmail(lowerCaseEmail)) {
      throw new ConflictException("Email already registered");
    }

    if (repo.existsByCpf(cpfWithoutMask)) {
      throw new ConflictException("CPF already registered");
    }

    var entity = UserMapper.toEntity(dto, encoder.encode(dto.getSenha()));
    var saved = repo.save(entity);
    
    return UserMapper.toResponse(saved);
  }

 
}

