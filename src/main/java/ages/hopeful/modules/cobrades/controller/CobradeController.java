package ages.hopeful.modules.cobrades.controller;

import ages.hopeful.modules.cobrades.dto.CobradeResponseDTO;
import ages.hopeful.modules.cobrades.service.CobradeService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cobrades")
public class CobradeController {

    private final CobradeService service;

    public CobradeController(CobradeService service) {
        this.service = service;
    }

    @GetMapping
    @ApiResponse(
        responseCode = "200",
        description = "Cobrades retrieved successfully"
    )
    public ResponseEntity<List<CobradeResponseDTO>> getAllCobrades() {
        return ResponseEntity.ok(service.getAllCobrades());
    }

    @GetMapping("/type/{type}")
    @ApiResponse(
        responseCode = "200",
        description = "Cobrades of type retrieved successfully"
    )
    public ResponseEntity<List<CobradeResponseDTO>> getAllCobradesByType(
        @PathVariable String type
    ) {
        return ResponseEntity.ok(service.findAllByType(type));
    }

    @GetMapping("/subtype/{subtype}")
    @ApiResponse(
        responseCode = "200",
        description = "Cobrades of subtype retrieved successfully"
    )
    public ResponseEntity<List<CobradeResponseDTO>> getAllCobradesBySubtype(
        @PathVariable String subtype
    ) {
        return ResponseEntity.ok(service.findAllBySubtype(subtype));
    }

    @GetMapping("/subgroup/{subgroup}")
    @ApiResponse(
        responseCode = "200",
        description = "Cobrades of subgroup retrieved successfully"
    )
    public ResponseEntity<List<CobradeResponseDTO>> getAllCobradesBySubgroup(
        @PathVariable String subgroup
    ) {
        return ResponseEntity.ok(service.findAllBySubgroup(subgroup));
    }

    @GetMapping("/code/{code}")
    @ApiResponse(
        responseCode = "200",
        description = "Cobrades of code retrieved successfully"
    )
    public ResponseEntity<List<CobradeResponseDTO>> getAllCobradesByCode(
        @PathVariable String code
    ) {
        return ResponseEntity.ok(service.findAllByCode(code));
    }
}
/*
    @GetMapping("/{id}")
    public ResponseEntity<CobradeResponseDTO> getCobradeById(
        @PathVariable UUID id
    ) {
        return ResponseEntity.ok(service.getCobradeById(id));
    }
}
*/
