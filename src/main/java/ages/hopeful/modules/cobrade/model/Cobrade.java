package ages.hopeful.modules.cobrade.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.persistence.*; //check it out later

@Entity
@Table(name = "cobrade")
@Data
public class Cobrade{

    @Id
    @Column(name = "code", length = 10)
    private String code;

    @Column(name = "group", nullable = false, length = 100)
    private String group;

    @Column(name = "subgroup", nullable = false, length = 100)
    private String subgroup;

    @Column(name = "type", nullable = false, length = 100)
    private String type;

    @Column(name = "subtype", length = 100)
    private String subtype;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "active")
    private Boolean active = true;
}