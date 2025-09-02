package ages.hopeful.modules.user.service;

import ages.hopeful.modules.user.dto.*;
import ages.hopeful.modules.user.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
  private final UserRepository repository;

  public UserService(UserRepository repository) { 
    this.repository = repository; 
  }

  public Object save(UserCreateDTO userCreateDTO) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'save'");
  }

}

