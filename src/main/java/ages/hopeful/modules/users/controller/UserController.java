package ages.hopeful.modules.users.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ages.hopeful.modules.users.dto.*;
import ages.hopeful.modules.users.services.UserService;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuários", description = "Gerenciamento de usuários")
public class UserController {
  private final UserService service;
  
  public UserController(UserService service) { 
    this.service = service; 
  }

  @PostMapping
  public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserCreateDTO userCreateDTO) {
    return ResponseEntity.ok(service.save(userCreateDTO));
  }

  @PutMapping ("/{id}")
    public ResponseEntity<UserResponseDTO> editUser(@PathVariable UUID id, @RequestBody @Valid UserUpdateDTO userUpdateDTO){
      return ResponseEntity.ok(service.save(userUpdateDTO));
  }
}

