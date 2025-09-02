package ages.hopeful.modules.user.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ages.hopeful.modules.user.dto.*;
import ages.hopeful.modules.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

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
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateDTO dto) {
        UserResponseDTO response = userService.createUser(dto);
        return ResponseEntity.ok(response);
    }
}
