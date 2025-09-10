package ages.hopeful.modules.scenarios.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Parameters")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"scenario"})
public class Parameter {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "action", nullable = false, length = 255)
    private String action;

    @Column(name = "fase", nullable = false, length = 255)
    private String fase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id", nullable = false)
    private Scenario scenario;
}
