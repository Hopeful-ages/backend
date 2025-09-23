package ages.hopeful.modules.scenarios.repository;

import ages.hopeful.modules.scenarios.model.Scenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScenarioRepository extends JpaRepository<Scenario, UUID> {
    Optional<Scenario> findByCobradeIdAndCityId(UUID id, UUID id1);
}
