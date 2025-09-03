package ages.hopeful.modules.user.service;

import ages.hopeful.modules.user.dto.*;
import ages.hopeful.modules.user.repository.UserRepository;
import ages.hopeful.modules.user.model.User;
import org.modelmapper.ModelMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
  private final UserRepository repository;
  private final ModelMapper modelMapper;

  public UserService(UserRepository repository, ModelMapper modelMapper) {
    this.repository = repository;
    this.modelMapper = modelMapper;
  }

  // create/update methods will be added later when request DTO is finalized

  // List users with optional status filter; ordered by name
  @Transactional(readOnly = true)
  public java.util.List<UserResponseDTO> list(String status) {
    java.util.List<User> users;
    if (status == null || status.isBlank()) {
      users = repository.findAllByOrderByNameAsc();
    } else {
      String s = status.trim().toLowerCase(java.util.Locale.ROOT);
      Boolean filter;
      switch (s) {
        case "active" -> filter = Boolean.TRUE;
        case "inactive" -> filter = Boolean.FALSE;
        default -> filter = null;
      }
      if (filter == null) {
        users = java.util.List.of();
      } else {
        users = repository.findByAccountStatusOrderByNameAsc(filter);
      }
    }

    return users.stream()
        .map(u -> modelMapper.map(u, UserResponseDTO.class))
        .toList();
  }

}
