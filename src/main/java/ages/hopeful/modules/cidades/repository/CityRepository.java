package ages.hopeful.modules.cidades.repository;

import ages.hopeful.modules.cidades.model.City;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, UUID> {}
