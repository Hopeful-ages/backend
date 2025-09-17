package ages.hopeful.modules.cobrade.service;

import ages.hopeful.modules.cobrade.dto.CobradeFilterDTO;
import ages.hopeful.modules.cobrade.dto.CobradeResponseDTO;
import ages.hopeful.modules.cobrade.entity.CobradeEntity;
import ages.hopeful.modules.cobrade.repository.CobradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CobradeService {

    private final CobradeRepository cobradeRepository;


    //Active COBRADE codes
    public List<CobradeResponseDTO> allActive() {
        log.info("Todos os códigos COBRADE ativos");

        List<CobradeEntity> entities = cobradeRepository.findByActiveTrue();

        return entities.stream()
                .map(CobradeResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    //Filter COBRADE by codes
    public List<CobradeResponseDTO> listarComFiltros(CobradeFilterDTO filter) {
        log.info("Códigos COBRADE filtrado: {}", filter);

        List<CobradeEntity> entities = cobradeRepository.findWithFilters(
                filter.getGrupo(),
                filter.getSubgrupo(),
                filter.getTipo(),
                filter.getCodigo(),
                filter.getAtivo()
        );

        return entities.stream()
                .map(CobradeResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    //Search a specific COBRADE code
    public Optional<CobradeResponseDTO> searchByCode(String code) {
        log.info("Buscando código COBRADE: {}", code);

        return cobradeRepository.findByCodigoIgnoreCaseAndAtivoTrue(code)
                .map(CobradeResponseDTO::fromEntity);
    }

    //Filter by group
    public List<CobradeResponseDTO> listByGroup(String group) {
        log.info("Listando códigos COBRADE do group: {}", group);

        List<CobradeEntity> entities = cobradeRepository.findByGroupIgnoreCaseAndActiveTrue(group);

        return entities.stream()
                .map(CobradeResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    //Filter by subgroup
    public List<CobradeResponseDTO> listBySubgroup(String subgroup) {
        log.info("Listando códigos COBRADE do subgrupo: {}", subgroup);

        List<CobradeEntity> entities = cobradeRepository.findBySubgroupIgnoreCaseAndActiveTrue(subgroup);

        return entities.stream()
                .map(CobradeResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }


    //Filter by type
    public List<CobradeResponseDTO> listByType(String type) {
        log.info("Listando códigos COBRADE do tipo: {}", type);

        List<CobradeEntity> entities = cobradeRepository.findByTypeIgnoreCaseAndActiveTrue(type);

        return entities.stream()
                .map(CobradeResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}