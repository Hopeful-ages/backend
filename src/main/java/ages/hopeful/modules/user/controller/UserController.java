package ages.hopeful.modules.user.controller;

import ages.hopeful.modules.user.dto.*;
import ages.hopeful.modules.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Create User",
        description = "Creates a new user in the system"
    )
    @ApiResponse(
        responseCode = "200",
        description = "User created successfully"
    )
    @ApiResponse(
        responseCode = "409",
        description = "Email or CPF already exists"
    )
    @ApiResponse(responseCode = "400", description = "Invalid user data")
    public ResponseEntity<UserResponseDTO> createUser(
        @Valid @RequestBody UserRequestDTO dto
    ) {
        UserResponseDTO response = service.createUser(dto);
        return ResponseEntity.ok(response);
    }
}
