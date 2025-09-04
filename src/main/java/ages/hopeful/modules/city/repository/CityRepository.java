package ages.hopeful.modules.city.repository;

import ages.hopeful.modules.city.model.City;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, UUID> {}
