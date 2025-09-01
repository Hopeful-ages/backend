package ages.hopeful.modules.users.controllers;

import ages.hopeful.modules.users.dto.UserResponse;
import ages.hopeful.modules.users.services.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "Users")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping
    @Operation(summary = "List users", description = "Listagem de usuarios (mookado)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> list(
            @RequestParam(value = "status", required = false) String status) {
                if (!status.isEmpty() && !status.matches("^(ativo|inativo)$")) {
                  return ResponseEntity.badRequest().build();
                }
                return ResponseEntity.ok(usersService.list(status));
    }
}
