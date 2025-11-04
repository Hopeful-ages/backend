package ages.hopeful.modules.user.service;

import ages.hopeful.common.exception.ConflictException;
import ages.hopeful.common.exception.NotFoundException;
import ages.hopeful.config.security.jwt.JwtUtil;
import ages.hopeful.modules.city.repository.CityRepository;
import ages.hopeful.modules.departments.repository.DepartmentRepository;
import ages.hopeful.modules.user.dto.*;
import ages.hopeful.modules.user.model.Role;
import ages.hopeful.modules.user.model.User;
import ages.hopeful.modules.user.repository.RoleRepository;
import ages.hopeful.modules.user.repository.UserRepository;
import java.util.Locale;
import java.util.Set;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final DepartmentRepository departmentRepository;
    private final CityRepository cityRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

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

        if (userUpdateDTO.getEmail() != null &&
                userRepository.existsByEmail(userUpdateDTO.getEmail()) &&
                !Objects.equals(user.getEmail(), userUpdateDTO.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        if (userUpdateDTO.getCpf() != null &&
                userRepository.existsByCpf(userUpdateDTO.getCpf()) &&
                !Objects.equals(user.getCpf(), userUpdateDTO.getCpf())) {
            throw new ConflictException("CPF already exists");
        }

        if (userUpdateDTO.getPassword() != null &&
                userUpdateDTO.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password is invalid");
        }

        modelMapper.map(userUpdateDTO, user);
        enrichUser(user, userUpdateDTO);
        hashPassword(user, userUpdateDTO);

        User updatedUser = userRepository.save(user);

        return modelMapper.map(updatedUser, UserResponseDTO.class);
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO dto) {

        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validateProperty(dto, "cpf");
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException("CPF is invalid");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email already exists");
        }
        if (userRepository.existsByCpf(dto.getCpf())) {
            throw new ConflictException("CPF already exists");
        }

        var department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new NotFoundException("Service not found"));

        var city = cityRepository.findById(dto.getCityId())
                .orElseThrow(() -> new NotFoundException("City not found"));

        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new NotFoundException("Role not found"));

        User user = modelMapper.map(dto, User.class);
        user.setAccountStatus(true);
        user.setRole(role);
        user.setDepartment(department);
        user.setCity(city);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers(String status) {
        List<User> users;
        if (status == null || status.isBlank()) {
            users = userRepository.findAllByOrderByNameAsc();
        } else {
            String s = status.trim().toLowerCase(Locale.ROOT);
            Boolean filter;
            switch (s) {
                case "active" -> filter = Boolean.TRUE;
                case "inactive" -> filter = Boolean.FALSE;
                default -> filter = null;
            }
            if (filter == null) {
                users = List.of();
            } else {
                users = userRepository.findByAccountStatusOrderByNameAsc(filter);
            }
        }

        return users.stream()
                .map(u -> modelMapper.map(u, UserResponseDTO.class))
                .toList();
    }

    public UserResponseDTO getUserByToken(String token) {
        UUID userId = jwtUtil.getUserIdFromToken(token);
        return getUserById(userId);
    }

    @Transactional
    public void disableUser(UUID userId, Authentication authentication) {
        String currentUserEmail = authentication.getName();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getEmail().equals(currentUserEmail) || user.getRole().getName().equals("ADMIN")) {
            throw new IllegalArgumentException("Conta Admin não pode ser desabilitada");
        }

        user.setAccountStatus(false);
        userRepository.save(user);
    }

    @Transactional
    public void enableUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setAccountStatus(true);
        userRepository.save(user);
    }

    private void enrichUser(User user, UserUpdateDTO dto) {
        if (dto.getDepartmentId() != null) {
            var department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new NotFoundException("Department not found"));
            user.setDepartment(department);
        }
        if (dto.getCityId() != null) {
            var city = cityRepository.findById(dto.getCityId())
                    .orElseThrow(() -> new NotFoundException("City not found"));
            user.setCity(city);
        }
        if (dto.getRoleId() != null) {
            var role = roleRepository.findById(dto.getRoleId())
                    .orElseThrow(() -> new NotFoundException("Role not found"));
            user.setRole(role);
        }
    }

    private void hashPassword(User user, UserUpdateDTO dto) {
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
    }
}

