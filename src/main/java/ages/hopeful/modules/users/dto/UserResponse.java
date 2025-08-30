package ages.hopeful.modules.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private String estado;
    private String cidade;
    private String serviço;
    private boolean status;
}
