package ages.hopeful.modules.user.controller;

import ages.hopeful.modules.user.dto.*;
import ages.hopeful.modules.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getUserById(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> editUser(
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
        return ResponseEntity.created(URI.create("/api/users/" + response.getId())).body(response);
    }

    @GetMapping
    @Operation(summary = "List users", description = "List users with optional status filter: active/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(service.getAllUsers(status));
    }

    @GetMapping("/by-token/{token}")
    @Operation(summary = "Get User info by token",
            description = "Get User info by token")
    @ApiResponse(responseCode = "200", description = "User returned successfully")
    @ApiResponse(responseCode = "400", description = "User not found")
    public ResponseEntity<UserResponseDTO> getUserByToken( @PathVariable String token) {
        UserResponseDTO response = service.getUserByToken(token);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/disable/{id}")
    @Operation(summary = "Disable User by id",
            description = "Disable User by id")
    @ApiResponse(responseCode = "200", description = "User disable successfully")
    @ApiResponse(responseCode = "400", description = "User not found")
    public ResponseEntity<Void> disableUserById( @PathVariable UUID id) {
        service.disableUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/enable/{id}")
    @Operation(summary = "Enable User by id",
            description = "Enable User by id")
    @ApiResponse(responseCode = "200", description = "User enabled successfully")
    @ApiResponse(responseCode = "400", description = "User not found")
    public ResponseEntity<Void> enableUserById(@PathVariable UUID id) {
        service.enableUser(id);
        return ResponseEntity.noContent().build();
    }
}
