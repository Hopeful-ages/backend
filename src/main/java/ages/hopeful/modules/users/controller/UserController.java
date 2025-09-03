package ages.hopeful.modules.users.controller;

import ages.hopeful.modules.users.dto.*;
import ages.hopeful.modules.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuários", description = "Gerenciamento de usuários")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> editUser(
        @PathVariable UUID id,
        @RequestBody @Valid UserUpdateDTO userUpdateDTO
    ) {
        return ResponseEntity.ok(service.updateUser(id, userUpdateDTO));
    }
}
