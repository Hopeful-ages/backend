package ages.hopeful.modules.users.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter 
@Setter
@Table(name = "usuario",
       uniqueConstraints = {
         @UniqueConstraint(name = "uk_usuario_email", columnNames = "email"),
         @UniqueConstraint(name = "uk_usuario_cpf", columnNames = "cpf")
       })
public class User {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id = UUID.randomUUID();

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "cpf", nullable = false, length = 14)
    private String cpf;

    @Column(name = "email", nullable = false, length = 160)
    private String email;

    @Column(name = "telefone", length = 30)
    private String telefone;

    @Column(name = "senha", nullable = false, length = 120)
    private String senhaHash;

    @Column(name = "servico_id", columnDefinition = "uuid")
    private UUID servicoId;   

    @Column(name = "cidade_id", columnDefinition = "uuid")
    private UUID cidadeId;    
}
