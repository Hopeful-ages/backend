package ages.hopeful.modules.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "papel")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    @Id
    private UUID id;

    @Column(name = "nome", nullable = false, unique = true)
    private String name; 
}
