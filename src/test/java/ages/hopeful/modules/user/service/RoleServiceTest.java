package ages.hopeful.modules.user.service;

import ages.hopeful.factories.RoleFactory;
import ages.hopeful.modules.user.dto.RoleResponseDTO;
import ages.hopeful.modules.user.model.Role;
import ages.hopeful.modules.user.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RoleService roleService;

    private Role roleAdmin;
    private Role roleUser;
    private RoleResponseDTO roleResponseDTOAdmin;
    private RoleResponseDTO roleResponseDTOUser;

    @BeforeEach
    void setUp() {

        roleAdmin = RoleFactory.createAdminRole();
        roleUser = RoleFactory.createUserRole();

        roleResponseDTOAdmin = RoleResponseDTO.fromModel(roleAdmin);
        roleResponseDTOUser = RoleResponseDTO.fromModel(roleUser);
    }

    @Test
    void getAllRoles_ShouldReturnListOfRoleResponseDTOs_WhenRolesExist() {

        List<Role> roles = Arrays.asList(roleAdmin, roleUser);

        when(roleRepository.findAll()).thenReturn(roles);

        when(modelMapper.map(roleAdmin, RoleResponseDTO.class)).thenReturn(roleResponseDTOAdmin);
        when(modelMapper.map(roleUser, RoleResponseDTO.class)).thenReturn(roleResponseDTOUser);

        List<RoleResponseDTO> result = roleService.getAllRoles();

        assertEquals(2, result.size());
        assertEquals(roleResponseDTOAdmin.getName(), result.get(0).getName());
        assertEquals(roleResponseDTOUser.getName(), result.get(1).getName());

        verify(roleRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(roleAdmin, RoleResponseDTO.class);
        verify(modelMapper, times(1)).map(roleUser, RoleResponseDTO.class);
    }

    @Test
    void getAllRoles_ShouldReturnEmptyList_WhenNoRolesExist() {

        when(roleRepository.findAll()).thenReturn(Collections.emptyList());

        List<RoleResponseDTO> result = roleService.getAllRoles();

        assertTrue(result.isEmpty());

        verify(roleRepository, times(1)).findAll();
        verify(modelMapper, never()).map(any(Role.class), eq(RoleResponseDTO.class));
    }
}

