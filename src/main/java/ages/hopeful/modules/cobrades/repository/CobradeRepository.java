package ages.hopeful.modules.cobrades.repository;

import ages.hopeful.modules.cobrades.model.Cobrade;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CobradeRepository extends JpaRepository<Cobrade, UUID> {}
