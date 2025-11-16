package ages.hopeful.modules.auth.controller;

import ages.hopeful.modules.auth.dto.ForgotPasswordRequest;
import ages.hopeful.modules.auth.dto.LoginRequest;
import ages.hopeful.modules.auth.dto.ResetPasswordRequest;
import ages.hopeful.modules.auth.dto.TokenResponse;
import ages.hopeful.modules.auth.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.Collections;

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
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Solicitar recuperação de senha",
               description = "Envia um email com um link para redefinir a senha")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email de recuperação enviado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Email é obrigatório")
    })
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        if (forgotPasswordRequest.getEmail() == null || forgotPasswordRequest.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Email é obrigatório"));
        }

        authService.forgotPassword(forgotPasswordRequest);
        return ResponseEntity.ok(Collections.singletonMap("message", "Enviamos um link para redefinir sua senha"));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Redefinir senha",
               description = "Redefine a senha do usuário a partir de um token de recuperação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Link de recuperação inválido ou expirado ou senha fraca")
    })
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        if (resetPasswordRequest.getNewPassword() == null || resetPasswordRequest.getNewPassword().length() < 8) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Senha deve ter no mínimo 8 caracteres"));
        }

        try {
            authService.resetPassword(resetPasswordRequest);
            return ResponseEntity.ok(Collections.singletonMap("message", "Senha alterada com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
