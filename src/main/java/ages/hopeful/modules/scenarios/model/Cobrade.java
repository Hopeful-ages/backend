package ages.hopeful.modules.scenarios.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cobrade")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "scenarios")
public class Cobrade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "code", nullable = false, length = 255)
    private String code;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "group_name", nullable = false, length = 255)
    private String group;

    @Column(name = "subgroup", nullable = false, length = 255)
    private String subgroup;

    @Column(name = "type", nullable = false, length = 255)
    private String type;

    @Column(name = "subtype", nullable = false, length = 255)
    private String subType;

    @OneToMany(mappedBy = "cobrade", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Scenario> scenarios;
}
