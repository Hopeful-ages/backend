package ages.hopeful.modules.user.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ages.hopeful.modules.user.dto.*;
import ages.hopeful.modules.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")

public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create User", 
               description = "Creates a new user in the system")
    @ApiResponse(responseCode = "200", description = "User created successfully")
    @ApiResponse(responseCode = "409", description = "Email or CPF already exists")
    @ApiResponse(responseCode = "400", description = "Invalid user data")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO dto) {
        UserResponseDTO response = userService.createUser(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/token")
    @Operation(summary = "Get User info by token",
            description = "Get User info by token")
    @ApiResponse(responseCode = "200", description = "User returned successfully")
    @ApiResponse(responseCode = "400", description = "User not found")
    public ResponseEntity<UserResponseDTO> getUserByToken( @RequestParam String token) {
        UserResponseDTO response = userService.getUserByToken(token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/id")
    @Operation(summary = "Get User info by id",
            description = "Get User info by id")
    @ApiResponse(responseCode = "200", description = "User returned successfully")
    @ApiResponse(responseCode = "400", description = "User not found")
    public ResponseEntity<UserResponseDTO> getUserById( @RequestParam UUID id) {
        UserResponseDTO response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/disable")
    @Operation(summary = "Disable User by id",
            description = "Disable User by id")
    @ApiResponse(responseCode = "200", description = "User disable successfully")
    @ApiResponse(responseCode = "400", description = "User not found")
    public ResponseEntity<Void> disableUserById( @RequestParam UUID id) {
        userService.DisableUser(id);
        return ResponseEntity.ok().build();
    }
}
