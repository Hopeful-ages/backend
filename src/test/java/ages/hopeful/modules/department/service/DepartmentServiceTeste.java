package ages.hopeful.modules.department.service;
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

import ages.hopeful.modules.departments.dto.DepartmentRequestDTO;
import ages.hopeful.modules.departments.dto.DepartmentResponseDTO;
import ages.hopeful.modules.departments.model.Department;
import ages.hopeful.modules.departments.repository.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for the department service")

public class DepartmentServiceTeste {
    
    @Mock 
    private DepartmentRepository departmentRepository;
    @Mock
    private ModelMapper modelMapper;
    
    private ages.hopeful.modules.departments.service.DepartmentService departmentService;
    @BeforeEach
    void setUp() {
        departmentService = new ages.hopeful.modules.departments.service.DepartmentService(departmentRepository, modelMapper);
    }
    @Test
    @DisplayName("should return DepartmentNotFoundException when no service is found")
    void shouldThrowDepartmentNotFoundException() {
        UUID invalidId = UUID.randomUUID();
        when (departmentRepository.findById(invalidId))
            .thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () -> departmentService.getDepartmentById(invalidId));
            verify(departmentRepository).findById(invalidId);
        }

    @Test
    void shouldReturnDepartmentById() {
        UUID id = UUID.randomUUID();
        Department department = new Department();
        department.setId(id);
        department.setName("Serviço Teste");

        when(departmentRepository.findById(id)).thenReturn(Optional.of(department));

        
        Department result = departmentService.getDepartmentById(id);

        
        assertEquals(id, result.getId());
        assertEquals("Serviço Teste", result.getName());
        verify(departmentRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("should return all services")
    void shouldReturnAllDepartments() {
        List<Department> departments = new ArrayList<>();
        Department s1 = new Department();
        s1.setName("Serviço 1");
        Department s2 = new Department();
        s2.setName("Serviço 2");

        departments.add(s1);
        departments.add(s2);

        when(departmentRepository.findAll()).thenReturn(departments);
        when(modelMapper.map(any(Department.class), eq(DepartmentResponseDTO.class)))
                .thenReturn(new DepartmentResponseDTO(s1.getId(),"Serviço 1"))
                .thenReturn(new DepartmentResponseDTO(s2.getId(),"Serviço 2"));
        
        List<DepartmentResponseDTO> result = departmentService.getAllDepartments();

        
        assertEquals(2, result.size());
        assertEquals("Serviço 1", result.get(0).getName());
        assertEquals("Serviço 2", result.get(1).getName());
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("should create a new service")
    void shouldCreateDepartment() {
        Department departmentToSave = new Department();
        departmentToSave.setName("New Service");

        Department savedDepartment = new Department();
        savedDepartment.setId(UUID.randomUUID());
        savedDepartment.setName("New Service");

        when(departmentRepository.save(any(Department.class))).thenReturn(savedDepartment);
        when(modelMapper.map(any(Department.class), eq(DepartmentResponseDTO.class)))
                .thenReturn(new DepartmentResponseDTO(savedDepartment.getId(), "New Service"));

        
        DepartmentRequestDTO departmentDTO = new DepartmentRequestDTO();
        departmentDTO.setName("New Service");
        DepartmentResponseDTO result = departmentService.createDepartment(departmentDTO);

        
        assertEquals("New Service", result.getName());
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    @DisplayName("should delete department by id")
    void shouldDeleteDepartmentById() {
        UUID id = UUID.randomUUID();
        Department department = new Department();
        department.setId(id);
        department.setName("Serviço para deletar");

        when(departmentRepository.findById(id)).thenReturn(Optional.of(department));

        
        departmentService.deleteDepartment(id);

        
        verify(departmentRepository, times(1)).findById(id);
        verify(departmentRepository, times(1)).delete(department);
    }
}