package ages.hopeful.modules.scenarios.model;

import ages.hopeful.modules.services.model.Service;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"scenario", "service"})
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "phase", nullable = false, length = 255)
    private String phase;

    @Column(name = "date")
    private Date lastUpdateDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id", referencedColumnName = "id")
    private Service service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id", nullable = false)
    private Scenario scenario;
}
