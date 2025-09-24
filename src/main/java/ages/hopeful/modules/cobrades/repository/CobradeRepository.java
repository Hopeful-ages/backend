package ages.hopeful.modules.cobrades.repository;

import ages.hopeful.modules.cobrades.dto.CobradeResponseDTO;
import ages.hopeful.modules.cobrades.model.Cobrade;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CobradeRepository extends JpaRepository<Cobrade, UUID> {
    //public List<CobradeResponseDTO> findAllByGroup(String group);

    public List<CobradeResponseDTO> findAllBySubgroup(String subgroup);

    public List<CobradeResponseDTO> findAllByType(String type);

    public List<CobradeResponseDTO> findAllByCode(String code);
}
