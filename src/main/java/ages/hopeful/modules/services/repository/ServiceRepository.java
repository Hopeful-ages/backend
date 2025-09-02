package ages.hopeful.modules.services.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ages.hopeful.modules.services.model.ServiceEntity;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, UUID> {
    Optional<ServiceEntity> findById(UUID uuid);
}