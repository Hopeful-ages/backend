package ages.hopeful.modules.users.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ages.hopeful.modules.users.dto.*;
import ages.hopeful.modules.users.services.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService service;
  public UserController(UserService service){ this.service = service; }

  @PostMapping
  public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserCreateDTO userCreateDTO) {
    return ResponseEntity.ok(service.save(userCreateDTO));
  }
}

