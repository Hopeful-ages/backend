package ages.hopeful.modules.services.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ages.hopeful.modules.services.dto.ServiceRequestDTO;
import ages.hopeful.modules.services.dto.ServiceResponseDTO;
import ages.hopeful.modules.services.service.ServiceService;
import ages.hopeful.modules.user.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/services")
public class ServiceController {
    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping
    @Operation(summary = "Get All Services", 
               description = "Returns a list of all services available in the system")
    @ApiResponse(responseCode = "200", description = "Services retrieved successfully")
    public ResponseEntity<List<ServiceResponseDTO>> getAllServices() {
        List<ServiceResponseDTO> services = serviceService.getAllServices();
        return ResponseEntity.ok(services);
    }
    @PostMapping()
    public ResponseEntity<ServiceResponseDTO> createService(@RequestBody @Valid ServiceRequestDTO serviceDTO) {
        
        ServiceResponseDTO response = serviceService.createService(serviceDTO);
        return ResponseEntity.created(URI.create("/api/service/" + response.getId())).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable UUID id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
    
}
