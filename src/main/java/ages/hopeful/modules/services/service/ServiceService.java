package ages.hopeful.modules.services.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import ages.hopeful.modules.services.dto.ServiceResponseDTO;
import ages.hopeful.modules.services.repository.ServiceRepository;

@Service
public class ServiceService {
    private final ServiceRepository serviceRepository;
    private final ModelMapper modelMapper;

    public ServiceService(ServiceRepository serviceRepository, ModelMapper modelMapper) {
        this.serviceRepository = serviceRepository;
        this.modelMapper = modelMapper;
    }

    public List<ServiceResponseDTO> getAllServices() {
        List<ages.hopeful.modules.services.model.Service> services = serviceRepository.findAll();
        return services.stream()
                .map(service -> modelMapper.map(service, ServiceResponseDTO.class))
                .toList();
    }
}
