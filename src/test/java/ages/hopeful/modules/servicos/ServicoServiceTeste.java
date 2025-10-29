package ages.hopeful.modules.servicos;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import ages.hopeful.modules.services.dto.ServiceResponseDTO;
import ages.hopeful.modules.services.model.Service;
import ages.hopeful.modules.services.repository.ServiceRepository;
import jakarta.persistence.EntityNotFoundException;
import ages.hopeful.modules.services.dto.ServiceResponseDTO;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for the service service")

public class ServicoServiceTeste {
    
    @Mock 
    private ServiceRepository serviceRepository;
    @Mock
    private ModelMapper modelMapper;
    
    private ages.hopeful.modules.services.service.ServiceService serviceService;
    @BeforeEach
    void setUp() {
        serviceService = new ages.hopeful.modules.services.service.ServiceService(serviceRepository, modelMapper);
    }
    @Test
    @DisplayName("should return ServiceNotFoundException when no service is found")
    void shouldThrowServiceNotFoundException() {
        UUID invalidId = UUID.randomUUID();
        when (serviceRepository.findById(invalidId))
            .thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () -> serviceService.getServiceById(invalidId));
            verify(serviceRepository).findById(invalidId);
        }

    @Test
    void shouldReturnServiceById() {
        // Arrange
        UUID id = UUID.randomUUID();
        Service service = new Service();
        service.setId(id);
        service.setName("Serviço Teste");

        when(serviceRepository.findById(id)).thenReturn(Optional.of(service));

        // Act
        Service result = serviceService.getServiceById(id);

        // Assert
        assertEquals(id, result.getId());
        assertEquals("Serviço Teste", result.getName());
        verify(serviceRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("should return all services")
    void shouldReturnAllServices() {
        // Arrange
        List<Service> services = new ArrayList<>();
        Service s1 = new Service();
        s1.setName("Serviço 1");
        Service s2 = new Service();
        s2.setName("Serviço 2");

        services.add(s1);
        services.add(s2);

        when(serviceRepository.findAll()).thenReturn(services);
        when(modelMapper.map(any(Service.class), eq(ServiceResponseDTO.class)))
                .thenReturn(new ServiceResponseDTO(s1.getId(),"Serviço 1"))
                .thenReturn(new ServiceResponseDTO(s2.getId(),"Serviço 2"));
        // Act
        List<ServiceResponseDTO> result = serviceService.getAllServices();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Serviço 1", result.get(0).getName());
        assertEquals("Serviço 2", result.get(1).getName());
        verify(serviceRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("should create a new service")
    void shouldCreateService() {
        // Arrange
        Service serviceToSave = new Service();
        serviceToSave.setName("New Service");

        Service savedService = new Service();
        savedService.setId(UUID.randomUUID());
        savedService.setName("New Service");

        when(serviceRepository.save(any(Service.class))).thenReturn(savedService);
        when(modelMapper.map(any(Service.class), eq(ServiceResponseDTO.class)))
                .thenReturn(new ServiceResponseDTO(savedService.getId(), "New Service"));

        // Act
        ages.hopeful.modules.services.dto.ServiceRequestDTO serviceDTO = new ages.hopeful.modules.services.dto.ServiceRequestDTO();
        serviceDTO.setName("New Service");
        ServiceResponseDTO result = serviceService.createService(serviceDTO);

        // Assert
        assertEquals("New Service", result.getName());
        verify(serviceRepository, times(1)).save(any(Service.class));
    }

    @Test
    @DisplayName("should delete service by id")
    void shouldDeleteServiceById() {
        // Arrange
        UUID id = UUID.randomUUID();
        Service service = new Service();
        service.setId(id);
        service.setName("Serviço para deletar");

        when(serviceRepository.findById(id)).thenReturn(Optional.of(service));

        // Act
        serviceService.deleteService(id);

        // Assert
        verify(serviceRepository, times(1)).findById(id);
        verify(serviceRepository, times(1)).delete(service);
    }
}