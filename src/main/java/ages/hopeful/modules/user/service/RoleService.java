package ages.hopeful.modules.user.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import ages.hopeful.modules.user.dto.RoleResponseDTO;
import ages.hopeful.modules.user.model.Role;
import ages.hopeful.modules.user.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    public RoleService(RoleRepository roleRepository, ModelMapper modelMapper) {
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }

    public List<RoleResponseDTO> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(role -> modelMapper.map(role, RoleResponseDTO.class))
                .toList();
    }   
}
