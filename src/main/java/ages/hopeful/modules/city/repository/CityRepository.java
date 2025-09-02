package ages.hopeful.modules.city.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ages.hopeful.modules.city.model.City;


@Repository
public interface CityRepository extends JpaRepository<City, UUID> {
}
