package ages.hopeful.modules.users.controller;

import jakarta.validation.Valid;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;


import ages.hopeful.modules.users.dto.*;
import ages.hopeful.modules.users.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;

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
    public ResponseEntity<String> editUser(@PathVariable UUID id, @RequestBody @Valid UserUpdateDTO userUpdateDTO){
      return ResponseEntity.ok("User updated successfully");
  }
}

