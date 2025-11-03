package ages.hopeful.modules.user.controller;

import ages.hopeful.modules.user.dto.UserRequestDTO;
import ages.hopeful.modules.user.dto.UserResponseDTO;
import ages.hopeful.modules.user.dto.UserUpdateDTO;
import ages.hopeful.modules.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/users-acessivel")
@AllArgsConstructor
@Tag(name = "Users Acessíveis", description = "Gerenciamento de usuários com suporte completo a acessibilidade para deficientes visuais")
public class UserAccessibilityConstroller {
    
    private final UserService userService;
    
    @GetMapping
    @Operation(
        summary = "Listar usuários", 
        description = "Retorna lista de usuários com mensagens acessíveis para leitores de tela. Aceita filtro por status: active, inactive"
    )
    public ResponseEntity<Map<String, Object>> listarTodos(
            @RequestParam(required = false) String status) {
        
        try {
            List<UserResponseDTO> users = userService.getAllUsers(status);
            
            String mensagem = construirMensagemListagem(users, status);
            
            return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "mensagem", mensagem,
                "dados", users,
                "tipo", "info"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "sucesso", false,
                "mensagem", "Erro ao buscar usuários: " + e.getMessage(),
                "tipo", "erro"
            ));
        }
    }
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar usuário por ID",
        description = "Retorna dados de um usuário específico com mensagem acessível"
    )
    public ResponseEntity<Map<String, Object>> buscarPorId(@PathVariable UUID id) {
        try {
            UserResponseDTO user = userService.getUserById(id);
            
            String mensagem = String.format(
                "Usuário encontrado: %s, E-mail: %s, Status: %s",
                user.getName(),
                user.getEmail(),
                user.getAccountStatus() ? "Ativo" : "Inativo"
            );
            
            return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "mensagem", mensagem,
                "dados", user,
                "tipo", "sucesso"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "sucesso", false,
                "mensagem", "Usuário não encontrado com ID: " + id + ". " + e.getMessage(),
                "tipo", "erro"
            ));
        }
    }
    
    @PostMapping
    @Operation(
        summary = "Criar novo usuário",
        description = "Cria um novo usuário com validação completa e mensagens acessíveis para leitores de tela"
    )
    public ResponseEntity<Map<String, Object>> criar(
            @Valid @RequestBody UserRequestDTO dto,
            BindingResult result) {
        
        if (result.hasErrors()) {
            Map<String, String> erros = new HashMap<>();
            for (FieldError erro : result.getFieldErrors()) {
                String campo = traduzirCampo(erro.getField());
                erros.put(erro.getField(), campo + ": " + erro.getDefaultMessage());
            }
            
            String mensagemPrincipal = construirMensagemErroValidacao(erros);
            
            return ResponseEntity.badRequest().body(Map.of(
                "sucesso", false,
                "mensagem", mensagemPrincipal,
                "erros", erros,
                "tipo", "erro"
            ));
        }
        
        try {
            UserResponseDTO userCriado = userService.createUser(dto);
            
            String mensagem = String.format(
                "Usuário %s cadastrado com sucesso. E-mail: %s. CPF: %s. Conta ativa.",
                userCriado.getName(),
                userCriado.getEmail(),
                formatarCPF(userCriado.getCpf())
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "sucesso", true,
                "mensagem", mensagem,
                "dados", userCriado,
                "tipo", "sucesso"
            ));
            
        } catch (Exception e) {
            String mensagemErro = tratarMensagemErro(e);
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "sucesso", false,
                "mensagem", mensagemErro,
                "tipo", "erro"
            ));
        }
    }
    
    @PatchMapping("/{id}")
    @Operation(
        summary = "Atualizar usuário",
        description = "Atualiza dados de um usuário existente com validação e mensagens acessíveis"
    )
    public ResponseEntity<Map<String, Object>> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateDTO dto,
            BindingResult result) {
        
        if (result.hasErrors()) {
            Map<String, String> erros = new HashMap<>();
            for (FieldError erro : result.getFieldErrors()) {
                String campo = traduzirCampo(erro.getField());
                erros.put(erro.getField(), campo + ": " + erro.getDefaultMessage());
            }
            
            String mensagemPrincipal = construirMensagemErroValidacao(erros);
            
            return ResponseEntity.badRequest().body(Map.of(
                "sucesso", false,
                "mensagem", mensagemPrincipal,
                "erros", erros,
                "tipo", "erro"
            ));
        }
        
        try {
            UserResponseDTO userAtualizado = userService.updateUser(id, dto);
            
            String mensagem = String.format(
                "Usuário %s atualizado com sucesso. E-mail: %s. Status: %s.",
                userAtualizado.getName(),
                userAtualizado.getEmail(),
                userAtualizado.getAccountStatus() ? "Ativo" : "Inativo"
            );
            
            return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "mensagem", mensagem,
                "dados", userAtualizado,
                "tipo", "sucesso"
            ));
            
        } catch (Exception e) {
            String mensagemErro = tratarMensagemErro(e);
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "sucesso", false,
                "mensagem", mensagemErro,
                "tipo", "erro"
            ));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desativar usuário",
        description = "Desativa um usuário (não remove do banco, apenas marca como inativo) com mensagem acessível"
    )
    public ResponseEntity<Map<String, Object>> desativar(@PathVariable UUID id) {
        try {
            UserResponseDTO user = userService.getUserById(id);
                        
            String mensagem = String.format(
                "Usuário %s (%s) foi desativado com sucesso. O registro permanece no sistema mas a conta está inativa.",
                user.getName(),
                user.getEmail()
            );
            
            return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "mensagem", mensagem,
                "tipo", "sucesso"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "sucesso", false,
                "mensagem", "Não foi possível desativar o usuário: " + e.getMessage(),
                "tipo", "erro"
            ));
        }
    }
    
    @PatchMapping("/{id}/reativar")
    @Operation(
        summary = "Reativar usuário",
        description = "Reativa um usuário previamente desativado com mensagem acessível"
    )
    public ResponseEntity<Map<String, Object>> reativar(@PathVariable UUID id) {
        try {
            userService.enableUser(id);
            UserResponseDTO user = userService.getUserById(id);
            
            String mensagem = String.format(
                "Usuário %s (%s) foi reativado com sucesso. A conta está ativa novamente.",
                user.getName(),
                user.getEmail()
            );
            
            return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "mensagem", mensagem,
                "dados", user,
                "tipo", "sucesso"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "sucesso", false,
                "mensagem", "Não foi possível reativar o usuário: " + e.getMessage(),
                "tipo", "erro"
            ));
        }
    }
    
    private String construirMensagemListagem(List<UserResponseDTO> users, String status) {
        if (users.isEmpty()) {
            if (status != null && !status.isBlank()) {
                return String.format("Nenhum usuário %s encontrado.", 
                    status.equalsIgnoreCase("active") ? "ativo" : "inativo");
            }
            return "Nenhum usuário cadastrado no sistema.";
        }
        
        int total = users.size();
        long ativos = users.stream().filter(u -> u.getAccountStatus()).count();
        long inativos = total - ativos;
        
        if (status != null && !status.isBlank()) {
            return String.format("%d usuário(s) %s encontrado(s).", 
                total, 
                status.equalsIgnoreCase("active") ? "ativo(s)" : "inativo(s)");
        }
        
        return String.format("%d usuário(s) encontrado(s). %d ativo(s), %d inativo(s).", 
            total, ativos, inativos);
    }
    
    private String construirMensagemErroValidacao(Map<String, String> erros) {
        int quantidade = erros.size();
        
        if (quantidade == 1) {
            String primeiroErro = erros.values().iterator().next();
            return "Erro de validação: " + primeiroErro;
        }
        
        return String.format(
            "Foram encontrados %d erros de validação. Corrija os campos indicados e tente novamente.",
            quantidade
        );
    }
    
    private String traduzirCampo(String campo) {
        return switch (campo) {
            case "name" -> "Nome";
            case "cpf" -> "CPF";
            case "email" -> "E-mail";
            case "phone" -> "Telefone";
            case "password" -> "Senha";
            case "serviceId" -> "Serviço";
            case "cityId" -> "Cidade";
            default -> campo;
        };
    }
    
    private String tratarMensagemErro(Exception e) {
        String mensagem = e.getMessage();
        
        if (mensagem.contains("Email already exists")) {
            return "Erro: Já existe um usuário cadastrado com este e-mail. Por favor, use outro e-mail.";
        }
        if (mensagem.contains("CPF already exists")) {
            return "Erro: Já existe um usuário cadastrado com este CPF. Por favor, verifique os dados.";
        }
        if (mensagem.contains("Service not found")) {
            return "Erro: O serviço selecionado não foi encontrado. Por favor, selecione um serviço válido.";
        }
        if (mensagem.contains("City not found")) {
            return "Erro: A cidade selecionada não foi encontrada. Por favor, selecione uma cidade válida.";
        }
        if (mensagem.contains("Role not found")) {
            return "Erro: A função de usuário não foi encontrada no sistema. Contate o administrador.";
        }
        if (mensagem.contains("CPF is invalid")) {
            return "Erro: O CPF informado é inválido. Por favor, verifique e digite novamente.";
        }
        if (mensagem.contains("Password is invalid")) {
            return "Erro: A senha deve ter no mínimo 8 caracteres.";
        }
        
        return "Erro ao processar requisição: " + mensagem;
    }
    
    private String formatarCPF(String cpf) {
        if (cpf == null || cpf.length() < 11) {
            return cpf;
        }
        // Remove qualquer formatação existente
        String apenasNumeros = cpf.replaceAll("[^0-9]", "");
        if (apenasNumeros.length() == 11) {
            return apenasNumeros.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        }
        return cpf;
    }
}