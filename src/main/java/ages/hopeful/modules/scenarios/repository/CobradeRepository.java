package ages.hopeful.modules.scenarios.repository;

import ages.hopeful.modules.cobrades.model.Cobrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CobradeRepository extends JpaRepository <Cobrade, UUID> {
}
