package ages.hopeful.modules.scenarios.service;

import ages.hopeful.modules.scenarios.dto.CobradeResponseDTO;
import ages.hopeful.modules.scenarios.model.Cobrade;
import ages.hopeful.modules.scenarios.repository.CobradeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CobradeService {
    private final CobradeRepository cobradeRepository;

    public List<CobradeResponseDTO> getAllCobrades() {
        return cobradeRepository.findAll()
                .stream()
                .map(CobradeResponseDTO::fromModel)
                .toList();
    }

    public Cobrade getCobradeById(UUID id) {
        return cobradeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cobrade não encontrado com id: " + id));
    }

}
