package ages.hopeful.modules.city.model;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cidade")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class City {

    @Id
    private UUID id;

    @Column(name = "nome", nullable = false)
    private String name;

    @Column(name = "estado", nullable = false, length = 100)
    private String states;
}
