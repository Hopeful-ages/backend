package ages.hopeful.modules.departments.service;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import ages.hopeful.modules.departments.dto.DepartmentRequestDTO;
import ages.hopeful.modules.departments.dto.DepartmentResponseDTO;
import ages.hopeful.modules.departments.model.Department;
import ages.hopeful.modules.departments.repository.DepartmentRepository;

@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;

    public DepartmentService(DepartmentRepository departmentRepository, ModelMapper modelMapper) {
        this.departmentRepository = departmentRepository;
        this.modelMapper = modelMapper;
    }

    public List<DepartmentResponseDTO> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        return departments.stream()
                .map(department -> modelMapper.map(department, DepartmentResponseDTO.class))
                .toList();
    }

    public Department getDepartmentById(UUID id){
        return departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado"));
    }
    @Transactional
    public DepartmentResponseDTO createDepartment(DepartmentRequestDTO departmentDTO) {
        Department department = departmentDTO.toModel();
        Department savedDepartment = departmentRepository.save(department);
        return modelMapper.map(savedDepartment, DepartmentResponseDTO.class);
    }
    @Transactional
    public void deleteDepartment(UUID id) {
        Department department = getDepartmentById(id);
        departmentRepository.delete(department);
    }
    
}
