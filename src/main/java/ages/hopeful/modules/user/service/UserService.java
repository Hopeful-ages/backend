package ages.hopeful.modules.user.service;

import ages.hopeful.common.exception.ConflictException;

import ages.hopeful.modules.user.dto.*;
import ages.hopeful.modules.user.mapper.UserMapper;
import ages.hopeful.modules.user.model.Role;
import ages.hopeful.modules.user.model.User;
import ages.hopeful.modules.user.repository.RoleRepository;
import ages.hopeful.modules.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  public UserService(UserRepository userRepository, RoleRepository roleRepository) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
  }
  @Transactional
    public UserResponseDTO createUser(UserCreateDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email already exists");
        }
        if (userRepository.existsByCpf(dto.getCpf())) {
            throw new ConflictException("CPF already exists");
        }

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
          User user = UserMapper.toEntity(dto);
          user.setAccountStatus(true);
          user.setRole(role);

          // senha já vem criptografada
          user.setPassword(dto.getPassword());

          User savedUser = userRepository.save(user);
          return UserMapper.toDto(savedUser);
    }

}

