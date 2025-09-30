package ages.hopeful.modules.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import ages.hopeful.common.exception.ConflictException;
import ages.hopeful.common.exception.NotFoundException;
import ages.hopeful.modules.city.model.City;
import ages.hopeful.modules.city.repository.CityRepository;
import ages.hopeful.modules.services.model.Service;
import ages.hopeful.modules.services.repository.ServiceRepository;
import ages.hopeful.modules.user.dto.UserRequestDTO;
import ages.hopeful.modules.user.dto.UserResponseDTO;
import ages.hopeful.modules.user.dto.UserUpdateDTO;
import ages.hopeful.modules.user.model.Role;
import ages.hopeful.modules.user.model.User;
import ages.hopeful.modules.user.repository.RoleRepository;
import ages.hopeful.modules.user.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for the user service")
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private CityRepository cityRepository;
    @Mock
    private PasswordEncoder passwordEncoder;


    private UserRequestDTO userRequestDTO;
    private User user;
    private UserResponseDTO userResponseDTO;
    private UserUpdateDTO userUpdateDTO;
    private Role role;
    private Service service;
    private City city;

    @BeforeEach
    void setUp() {
        UUID serviceId = UUID.randomUUID();
        UUID cityId = UUID.randomUUID();

        userRequestDTO = UserRequestDTO.builder()
            .name("Test User")
            .cpf("529.982.247-25")
            .email("test@example.com")
            .password("password123")
            .serviceId(serviceId)
            .cityId(cityId)
            .build();

        userUpdateDTO = UserUpdateDTO.builder()
            .name("Updated User")
            .cpf("987.654.321-00")
            .email("updated@example.com")
            .phone("555199999999")
            .serviceId(serviceId)
            .cityId(cityId)
            .password("newpassword123")
            .build();

        user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setEmail("test@example.com");

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.id = user.getId();
        userResponseDTO.name = user.getName();
        userResponseDTO.email = user.getEmail();

        role = new Role(UUID.randomUUID(), "USER");
        service = new Service();
        city = new City();
    }

    @Test
    @DisplayName("Should create a user successfully when data is valid")
    void shouldCreateUserSuccessfullyWhenDataIsValid() {
        // Mocking
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByCpf(anyString())).thenReturn(false);
        when(serviceRepository.findById(any(UUID.class))).thenReturn(
            Optional.of(service)
        );
        when(cityRepository.findById(any(UUID.class))).thenReturn(
            Optional.of(city)
        );
        when(roleRepository.findByName(anyString())).thenReturn(
            Optional.of(role)
        );
        when(
            modelMapper.map(any(UserRequestDTO.class), eq(User.class))
        ).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(
            modelMapper.map(any(User.class), eq(UserResponseDTO.class))
        ).thenReturn(userResponseDTO);

        UserResponseDTO response = userService.createUser(userRequestDTO);

        assertNotNull(response);
        assertEquals(userResponseDTO.id, response.id);
        assertEquals("Test User", response.name);

        verify(userRepository, times(1)).existsByEmail(
            userRequestDTO.getEmail()
        );
        verify(userRepository, times(1)).existsByCpf(userRequestDTO.getCpf());
        verify(serviceRepository, times(1)).findById(
            userRequestDTO.getServiceId()
        );
        verify(cityRepository, times(1)).findById(userRequestDTO.getCityId());
        verify(roleRepository, times(1)).findByName("USER");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should throw ConflictException when email already exists")
    void shouldThrowConflictExceptionWhenEmailExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(ConflictException.class, () ->
            userService.createUser(userRequestDTO)
        );

        verify(userRepository, times(1)).existsByEmail(
            userRequestDTO.getEmail()
        );
        verify(userRepository, never()).existsByCpf(anyString());
    }

    @Test
    @DisplayName("Should throw ConflictException when CPF already exists")
    void shouldThrowConflictExceptionWhenCpfExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByCpf(anyString())).thenReturn(true);

        assertThrows(ConflictException.class, () ->
            userService.createUser(userRequestDTO)
        );

        verify(userRepository, times(1)).existsByEmail(
            userRequestDTO.getEmail()
        );
        verify(userRepository, times(1)).existsByCpf(userRequestDTO.getCpf());
        verify(serviceRepository, never()).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should throw NotFoundException when Service not found")
    void shouldThrowNotFoundExceptionWhenServiceNotFound() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByCpf(anyString())).thenReturn(false);
        when(serviceRepository.findById(any(UUID.class))).thenReturn(
            Optional.empty()
        );

        assertThrows(NotFoundException.class, () ->
            userService.createUser(userRequestDTO)
        );

        verify(userRepository, times(1)).existsByEmail(
            userRequestDTO.getEmail()
        );
        verify(userRepository, times(1)).existsByCpf(userRequestDTO.getCpf());
        verify(serviceRepository, times(1)).findById(
            userRequestDTO.getServiceId()
        );
        verify(cityRepository, never()).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should throw NotFoundException when City not found")
    void shouldThrowNotFoundExceptionWhenCityNotFound() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByCpf(anyString())).thenReturn(false);
        when(serviceRepository.findById(any(UUID.class))).thenReturn(
            Optional.of(service)
        );
        when(cityRepository.findById(any(UUID.class))).thenReturn(
            Optional.empty()
        );

        assertThrows(NotFoundException.class, () ->
            userService.createUser(userRequestDTO)
        );

        verify(userRepository, times(1)).existsByEmail(
            userRequestDTO.getEmail()
        );
        verify(userRepository, times(1)).existsByCpf(userRequestDTO.getCpf());
        verify(serviceRepository, times(1)).findById(
            userRequestDTO.getServiceId()
        );
        verify(cityRepository, times(1)).findById(userRequestDTO.getCityId());
        verify(roleRepository, never()).findByName(anyString());
    }

    @Test
    @DisplayName("Should update user successfully when data is valid")
    void shouldUpdateUserSuccessfullyWhenDataIsValid() {
        UUID userId = user.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByCpf(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        doAnswer(invocation -> {
            UserUpdateDTO dto = invocation.getArgument(0);
            User entity = invocation.getArgument(1);
            if (dto.getName() != null) entity.setName(dto.getName());
            if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
            if (dto.getCpf() != null) entity.setCpf(dto.getCpf());
            if (dto.getPassword() != null) entity.setPassword(dto.getPassword());
            return null;
        }).when(modelMapper).map(any(UserUpdateDTO.class), any(User.class));

        when(modelMapper.map(any(User.class), eq(UserResponseDTO.class)))
            .thenAnswer(invocation -> {
                User u = invocation.getArgument(0);
                UserResponseDTO dto = new UserResponseDTO();
                dto.setName(u.getName());
                dto.setEmail(u.getEmail());
                dto.setCpf(u.getCpf());
                return dto;
            });

        when(serviceRepository.findById(userUpdateDTO.getServiceId()))
            .thenReturn(Optional.of(service));

        when(cityRepository.findById(userUpdateDTO.getCityId()))
            .thenReturn(Optional.of(city));

        UserResponseDTO response = userService.updateUser(userId, userUpdateDTO);

        assertNotNull(response);
        assertEquals(user.getName(), response.getName());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getCpf(), response.getCpf());
        assertEquals(service, user.getService());
        assertEquals(city, user.getCity());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName(
        "Should throw ConflictException when updating email to one that already exists"
    )
    void shouldThrowConflictExceptionWhenUpdatingEmailToExistingOne() {
        UUID userId = user.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(userUpdateDTO.getEmail())).thenReturn(
            true
        );

        assertThrows(ConflictException.class, () ->
            userService.updateUser(userId, userUpdateDTO)
        );
    }

    @Test
    @DisplayName(
        "Should throw ConflictException when updating CPF to one that already exists"
    )
    void shouldThrowConflictExceptionWhenUpdatingCpfToExistingOne() {
        UUID userId = user.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByCpf(userUpdateDTO.getCpf())).thenReturn(
            true
        );
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        assertThrows(ConflictException.class, () ->
            userService.updateUser(userId, userUpdateDTO)
        );
    }

    @Test
    @DisplayName(
        "Should throw IllegalArgumentException when password is invalid"
    )
    void shouldThrowValidationExceptionWhenPasswordIsInvalid() {
        UUID userId = user.getId();
        userUpdateDTO.setPassword("123"); // senha muito curta

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () ->
            userService.updateUser(userId, userUpdateDTO)
        );
    }

    @Test
    @DisplayName(
        "Should throw NotFoundException when updating a non-existent user"
    )
    void shouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        UUID invalidId = UUID.randomUUID();
        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
            userService.updateUser(invalidId, userUpdateDTO)
        );
    }

    @Test
    @DisplayName("Should create user when CPF is valid")
    void shouldCreateUserWhenCpfIsValid() {
        
        userRequestDTO.setCpf("529.982.247-25");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByCpf(anyString())).thenReturn(false);
        when(serviceRepository.findById(any(UUID.class))).thenReturn(Optional.of(service));
        when(cityRepository.findById(any(UUID.class))).thenReturn(Optional.of(city));
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(role));
        when(modelMapper.map(any(UserRequestDTO.class), eq(User.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(any(User.class), eq(UserResponseDTO.class))).thenReturn(userResponseDTO);

        UserResponseDTO response = userService.createUser(userRequestDTO);

        assertNotNull(response);
        assertEquals(userResponseDTO.id, response.id);
        
        verify(userRepository, times(1)).existsByCpf(userRequestDTO.getCpf());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when CPF is invalid")
    void shouldThrowWhenCpfIsInvalid() {

        userRequestDTO.setCpf("123.456.789-00");
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(userRequestDTO));
        verify(userRepository, never()).save(any(User.class));
    }

}
