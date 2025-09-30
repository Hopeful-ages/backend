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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cobrades")
public class CobradeController {

    private final CobradeService service;

    public CobradeController(CobradeService service) {
        this.service = service;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CobradeResponseDTO> getCobradeById(
        @PathVariable UUID id
    ) {
        return ResponseEntity.ok(service.getCobradeById(id));
    }

    @GetMapping
    @ApiResponse(
        responseCode = "200",
        description = "Cobrades retrieved successfully"
    )
    public ResponseEntity<List<CobradeResponseDTO>> getAllCobrades() {
        return ResponseEntity.ok(service.getAllCobrades());
    }


    @GetMapping("/filter")
    public ResponseEntity<List<CobradeResponseDTO>> findAllFilter(
        @RequestParam(required = false) String type,
        @RequestParam(required = false) String subgroup,
        @RequestParam(required = false) String subtype,
        @RequestParam(required = false) String code
    ) {
        return ResponseEntity.ok(service.findAllFilter(type, subgroup, subtype, code));
    }
}
