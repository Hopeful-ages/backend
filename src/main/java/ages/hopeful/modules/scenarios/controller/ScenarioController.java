package ages.hopeful.modules.scenarios.controller;


import ages.hopeful.modules.scenarios.dto.ScenarioRequestDTO;
import ages.hopeful.modules.scenarios.dto.ScenarioResponseDTO;
import ages.hopeful.modules.scenarios.service.ScenarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/scenarios")
@Tag(name = "Scenarios", description = "Management of disaster scenarios")
public class ScenarioController {

    private final ScenarioService scenarioService;

    public ScenarioController(ScenarioService scenarioService) {
        this.scenarioService = scenarioService;
    }

    @GetMapping
    @Operation(summary = "Get All Scenarios",
            description = "Returns a list of all scenarios with tasks and parameters")
    @ApiResponse(responseCode = "200", description = "Scenarios retrieved successfully")
    public ResponseEntity<List<ScenarioResponseDTO>> getAllScenarios() {
        return ResponseEntity.ok(scenarioService.getAllScenarios());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Scenario by ID",
            description = "Returns a single scenario by its ID")
    @ApiResponse(responseCode = "200", description = "Scenario retrieved successfully")
    public ResponseEntity<ScenarioResponseDTO> getScenarioById(@PathVariable UUID id) {
        return ResponseEntity.ok(scenarioService.getScenarioById(id));
    }

    @PostMapping
    @Operation(summary = "Create a Scenario",
            description = "Creates a new scenario including tasks and parameters")
    @ApiResponse(responseCode = "200", description = "Scenario created successfully")
    public ResponseEntity<ScenarioResponseDTO> createScenario(@RequestBody ScenarioRequestDTO request) {
        return ResponseEntity.ok(scenarioService.createScenario(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a Scenario",
            description = "Updates an existing scenario by ID")
    @ApiResponse(responseCode = "200", description = "Scenario updated successfully")
    public ResponseEntity<ScenarioResponseDTO> updateScenario(
            @PathVariable UUID id,
            @RequestBody ScenarioRequestDTO request,
            @RequestParam boolean isAdmin
    ) {
        return ResponseEntity.ok(scenarioService.updateScenario(id, request, isAdmin));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Scenario",
            description = "Deletes a scenario by its ID")
    @ApiResponse(responseCode = "204", description = "Scenario deleted successfully")
    public ResponseEntity<Void> deleteScenario(@PathVariable UUID id) {
        scenarioService.deleteScenario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-city-cobrade")
    @Operation(summary = "Get Scenario by City and Cobrade",
            description = "Returns a scenario by cityId and cobradeId")
    @ApiResponse(responseCode = "200", description = "Scenario retrieved successfully")
    public ResponseEntity<ScenarioResponseDTO> getScenarioByCityAndCobrade(
            @RequestParam UUID cityId,
            @RequestParam UUID cobradeId
    ) {
        return ResponseEntity.ok(scenarioService.getScenarioByCityAndCobrade(cityId, cobradeId));
    }
}
