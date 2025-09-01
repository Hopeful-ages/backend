package ages.hopeful.modules.cidades.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ages.hopeful.modules.cidades.model.Cidade;

import java.util.UUID;


@Repository
public interface CityRepository extends JpaRepository<City, UUID> {
}