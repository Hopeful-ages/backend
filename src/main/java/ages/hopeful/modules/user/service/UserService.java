package ages.hopeful.modules.user.service;

import ages.hopeful.common.exception.ConflictException;
import ages.hopeful.common.exception.NotFoundException;
import ages.hopeful.config.security.jwt.JwtUtil;
import ages.hopeful.modules.city.repository.CityRepository;
import ages.hopeful.modules.services.repository.ServiceRepository;
import ages.hopeful.modules.user.dto.*;
import ages.hopeful.modules.user.model.Role;
import ages.hopeful.modules.user.model.User;
import ages.hopeful.modules.user.repository.RoleRepository;
import ages.hopeful.modules.user.repository.UserRepository;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Service
public class UserService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final ModelMapper modelMapper;
  private final ServiceRepository serviceRepository;
  private final CityRepository cityRepository;
  private final JwtUtil jwtUtil;


    @Transactional
    public UserResponseDTO getUserById(UUID id) {
        
        User user = userRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("User not found"));
        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Transactional
    public UserResponseDTO updateUser(UUID id, UserUpdateDTO userUpdateDTO) {
        User user = userRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("User not found"));

        if (
            userUpdateDTO.getEmail() != null &&
            userRepository.existsByEmail(userUpdateDTO.getEmail()) &&
            !Objects.equals(user.getEmail(), userUpdateDTO.getEmail())
        ) {
            throw new ConflictException("Email already exists");
        }

        if (
            userUpdateDTO.getCpf() != null &&
            userRepository.existsByCpf(userUpdateDTO.getCpf()) &&
            !Objects.equals(user.getCpf(), userUpdateDTO.getCpf())
        ) {
            throw new ConflictException("CPF already exists");
        }

        if (
            userUpdateDTO.getPassword() != null &&
            userUpdateDTO.getPassword().length() < 8
        ) {
            throw new IllegalArgumentException("Password is invalid");
        }

        modelMapper.map(userUpdateDTO, user);
        User updatedUser = userRepository.save(user);

        return modelMapper.map(updatedUser, UserResponseDTO.class);
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email already exists");
        }
        if (userRepository.existsByCpf(dto.getCpf())) {
            throw new ConflictException("CPF already exists");
        }

        var service = serviceRepository.findById(dto.getServiceId())
            .orElseThrow(() -> new NotFoundException("Service not found"));

        var city = cityRepository.findById(dto.getCityId())
            .orElseThrow(() -> new NotFoundException("City not found"));

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new NotFoundException("Role not found"));

        User user = modelMapper.map(dto, User.class);
        user.setAccountStatus(true);
        user.setRole(role);
        user.setService(service);
        user.setCity(city);
        user.setPassword(dto.getPassword());

        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponseDTO.class);
    }


    public UserResponseDTO getUserByToken(String token){
        UUID userId = jwtUtil.getUserIdFromToken(token);
        return getUserById(userId);
    }

    @Transactional
    public void disableUser (UUID userId){
      userRepository.disableUserById(userId);

    }
    @Transactional
    public void enableUser(UUID userId){
        userRepository.enableUserById(userId);
    }

}

