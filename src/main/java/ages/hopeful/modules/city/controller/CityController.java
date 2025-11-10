package ages.hopeful.modules.city.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ages.hopeful.modules.city.service.CityService;
import ages.hopeful.modules.city.dto.CityResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/city")
@Tag(name = "Cities", description = "Management of cities")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    
    @GetMapping
    @Operation(summary = "Get All Cities", 
               description = "Returns a list of all cities available in the system")
    @ApiResponse(responseCode = "200", description = "Cities retrieved successfully")
    public ResponseEntity<List<CityResponseDTO>> getAllCities() {
        List<CityResponseDTO> cities = cityService.getAllCities();
        return ResponseEntity.ok(cities);
    }


    
}