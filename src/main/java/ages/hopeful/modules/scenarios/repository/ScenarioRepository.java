package ages.hopeful.modules.scenarios.repository;

import ages.hopeful.modules.scenarios.model.Scenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScenarioRepository extends JpaRepository<Scenario, UUID> {
    Optional<Scenario> findByCobradeIdAndCityId(UUID id, UUID id1);

    @Query("""
        SELECT s FROM Scenario s
        WHERE s.published = true
        AND (:cobradeId IS NULL OR s.cobrade.id = :cobradeId)
        AND (:cityId IS NULL OR s.city.id = :cityId)
    """)
    List<Scenario> findByCobradeIdAndCityIdSearch(
            @Param("cityId") UUID cityId,
            @Param("cobradeId") UUID cobradeId
    ) ;
}
