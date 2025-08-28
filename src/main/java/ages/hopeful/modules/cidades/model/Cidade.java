package ages.hopeful.modules.cidades.model;


import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "cidade")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cidade {

    @Id
    private UUID id;
    
    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "estado", nullable = false, length = 100)
    private String estado;
    
}