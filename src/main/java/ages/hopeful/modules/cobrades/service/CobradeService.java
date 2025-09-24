package ages.hopeful.modules.cobrades.service;

import ages.hopeful.modules.cobrades.dto.CobradeFilterDTO;
import ages.hopeful.modules.cobrades.dto.CobradeResponseDTO;
import ages.hopeful.modules.cobrades.model.Cobrade;
import ages.hopeful.modules.cobrades.repository.CobradeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CobradeService {

    private final CobradeRepository cobradeRepository;

    //Active COBRADE codes
    public List<CobradeResponseDTO> getAllCobrades() {
        return cobradeRepository
            .findAll()
            .stream()
            .map(CobradeResponseDTO::fromModel)
            .toList();
    }

    public Cobrade getCobradeById(UUID id) {
        return cobradeRepository
            .findById(id)
            .orElseThrow(() ->
                new EntityNotFoundException(
                    "Cobrade não encontrado com id: " + id
                )
            );
    }

    public List<CobradeResponseDTO> findAllBySubgroup(String subgroup) {
        return cobradeRepository
            .findAll()
            .stream()
            .filter(
                cobrade ->
                    cobrade.getSubgroup() != null &&
                    cobrade.getSubgroup().equalsIgnoreCase(subgroup)
            )
            .map(CobradeResponseDTO::fromModel)
            .toList();
    }

    public List<CobradeResponseDTO> findAllByType(String type) {
        return cobradeRepository
            .findAll()
            .stream()
            .filter(
                cobrade ->
                    cobrade.getType() != null &&
                    cobrade.getType().equalsIgnoreCase(type)
            )
            .map(CobradeResponseDTO::fromModel)
            .toList();
    }

    public List<CobradeResponseDTO> findAllByCode(String code) {
        return cobradeRepository
            .findAll()
            .stream()
            .filter(
                cobrade ->
                    cobrade.getCode() != null &&
                    cobrade.getCode().equalsIgnoreCase(code)
            )
            .map(CobradeResponseDTO::fromModel)
            .toList();
    }

    /* Métodos possivelmentes desnecessários */
    /*
    public List<CobradeResponseDTO> allActive() {
        List<Cobrade> entities = cobradeRepository.findByActiveTrue();

        return entities
            .stream()
            .map(CobradeResponseDTO::fromModel)
            .collect(Collectors.toList());
    }

    //Filter COBRADE by codes
    public List<CobradeResponseDTO> listarComFiltros(CobradeFilterDTO filter) {
        log.info("Códigos COBRADE filtrado: {}", filter);

        List<Cobrade> entities = cobradeRepository.findWithFilters(
            filter.getGrupo(),
            filter.getSubgrupo(),
            filter.getTipo(),
            filter.getCodigo(),
            filter.getAtivo()
        );

        return entities
            .stream()
            .map(CobradeResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    //Search a specific COBRADE code
    public Optional<CobradeResponseDTO> searchByCode(String code) {
        log.info("Buscando código COBRADE: {}", code);

        return cobradeRepository
            .findByCodigoIgnoreCaseAndAtivoTrue(code)
            .map(CobradeResponseDTO::fromEntity);
    }

    //Filter by group
    public List<CobradeResponseDTO> listByGroup(String group) {
        log.info("Listando códigos COBRADE do group: {}", group);

        List<CobradeEntity> entities =
            cobradeRepository.findByGroupIgnoreCaseAndActiveTrue(group);

        return entities
            .stream()
            .map(CobradeResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    //Filter by subgroup
    public List<CobradeResponseDTO> listBySubgroup(String subgroup) {
        log.info("Listando códigos COBRADE do subgrupo: {}", subgroup);

        List<CobradeEntity> entities =
            cobradeRepository.findBySubgroupIgnoreCaseAndActiveTrue(subgroup);

        return entities
            .stream()
            .map(CobradeResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    //Filter by type
    public List<CobradeResponseDTO> listByType(String type) {
        log.info("Listando códigos COBRADE do tipo: {}", type);

        List<CobradeEntity> entities =
            cobradeRepository.findByTypeIgnoreCaseAndActiveTrue(type);

        return entities
            .stream()
            .map(CobradeResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }
    */
}
