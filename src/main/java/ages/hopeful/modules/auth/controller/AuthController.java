package ages.hopeful.modules.auth.controller;

import ages.hopeful.modules.auth.dto.LoginRequest;
import ages.hopeful.modules.auth.dto.TokenResponse;
import ages.hopeful.modules.auth.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Tag(name = "Auth", description = "Endpoints de login e registro")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Realizar login", 
               description = "Autentica um usuário e retorna token JWT junto com informações do usuário (função, serviço, cidade)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        if (loginRequest.getUsername() == null || loginRequest.getUsername().isEmpty() ||
                loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Usuário e senha são obrigatórios.");
        }

        TokenResponse tokenResponse = authService.login(loginRequest);

        if (tokenResponse != null) {
            return ResponseEntity.ok(tokenResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciais inválidas.");
        }
    }

    // Endpoint para usuários com ROLE_USER
    @GetMapping("/user-info")
    @Operation(summary = "Informações do usuário", description = "Retorna dados visíveis apenas para usuários comuns")
    public ResponseEntity<String> getUserInfo() {
        return ResponseEntity.ok("Informações acessíveis para ROLE_USER");
    }

    // Endpoint para usuários com ROLE_ADMIN
    @GetMapping("/admin-info")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Informações do admin", description = "Retorna dados visíveis apenas para administradores")
    public ResponseEntity<String> getAdminInfo() {
        return ResponseEntity.ok("Informações acessíveis para ROLE_ADMIN");
    }
    
}
