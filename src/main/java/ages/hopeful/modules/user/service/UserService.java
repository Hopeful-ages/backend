package ages.hopeful.modules.user.service;

import ages.hopeful.common.exception.ConflictException;
import ages.hopeful.common.exception.NotFoundException;
import ages.hopeful.modules.city.repository.CityRepository;
import ages.hopeful.modules.services.repository.ServiceRepository;
import ages.hopeful.modules.user.dto.*;
import ages.hopeful.modules.user.model.Role;
import ages.hopeful.modules.user.model.User;
import ages.hopeful.modules.user.repository.RoleRepository;
import ages.hopeful.modules.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final ModelMapper modelMapper;
  private final ServiceRepository serviceRepository;
  private final CityRepository cityRepository;

  public UserService(UserRepository userRepository, RoleRepository roleRepository, ModelMapper modelMapper, ServiceRepository serviceRepository, CityRepository cityRepository) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.modelMapper = modelMapper;
    this.serviceRepository = serviceRepository;
    this.cityRepository = cityRepository;

  }
  @Transactional
    public UserResponseDTO createUser(UserRequestDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email already exists");
        }
        if (userRepository.existsByCpf(dto.getCpf())) {
            throw new ConflictException("CPF already exists");
        }
        if(serviceRepository.findById(dto.getServiceId()).isEmpty()){
            throw new NotFoundException("Service not found");
        }
        if (cityRepository.findById(dto.getCityId()).isEmpty()) {
            throw new NotFoundException("City not found");
        }

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new NotFoundException("Role not found"));
        User user = modelMapper.map(dto, User.class);
        user.setAccountStatus(true);
        user.setRole(role);
        user.setPassword(dto.getPassword());

        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponseDTO.class);
    }

}

