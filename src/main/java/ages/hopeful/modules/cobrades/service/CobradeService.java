package ages.hopeful.modules.cobrades.service;

import ages.hopeful.modules.cobrades.dto.CobradeResponseDTO;
import ages.hopeful.modules.cobrades.model.Cobrade;
import ages.hopeful.modules.cobrades.repository.CobradeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CobradeService {

    private final CobradeRepository cobradeRepository;

    public List<CobradeResponseDTO> getAllCobrades() {
        return cobradeRepository
            .findAll()
            .stream()
            .map(CobradeResponseDTO::fromModel)
            .toList();
    }

    @Transactional(readOnly=true)
    public List<CobradeResponseDTO> findAllFilter(String type,String subgroup,String subtype,String code) {


        if (type == null && subgroup == null && subtype == null && code == null) {
            return this.getAllCobrades();
        }

        Specification<Cobrade> spec = null;

        if (type != null) {
            Specification<Cobrade> typeSpec = (root, query, cb) ->
                    cb.equal(cb.lower(root.get("type")), type.toLowerCase());
            spec = (spec == null) ? typeSpec : spec.and(typeSpec);
        }

        if (subtype != null) {
            Specification<Cobrade> subtypeSpec = (root, query, cb) ->
                    cb.equal(cb.lower(root.get("subType")), subtype.toLowerCase());
            spec = (spec == null) ? subtypeSpec : spec.and(subtypeSpec);
        }

        if (subgroup != null) {
            Specification<Cobrade> subgroupSpec = (root, query, cb) ->
                    cb.equal(cb.lower(root.get("subgroup")), subgroup.toLowerCase());
            spec = (spec == null) ? subgroupSpec : spec.and(subgroupSpec);
        }

        if (code != null) {
            Specification<Cobrade> codeSpec = (root, query, cb) ->
                    cb.equal(cb.lower(root.get("code")), code.toLowerCase());
            spec = (spec == null) ? codeSpec : spec.and(codeSpec);
        }

        List<Cobrade> cobrades = cobradeRepository.findAll(spec);

        return cobrades.stream()
                .map(CobradeResponseDTO::fromModel)
                .collect(Collectors.toList());
  }

    @Transactional(readOnly=true)
    public CobradeResponseDTO getCobradeById(UUID id) {
        Cobrade cobrade = cobradeRepository
            .findById(id)
            .orElseThrow(() ->
                new EntityNotFoundException("Cobrade not found with id: " + id)
            );
        return CobradeResponseDTO.fromModel(cobrade);

    }

    public Cobrade getCobradeEntityById(UUID id) {
        return cobradeRepository
            .findById(id)
            .orElseThrow(() ->
                new EntityNotFoundException("Cobrade not found with id: " + id)
            );
    }

   
}