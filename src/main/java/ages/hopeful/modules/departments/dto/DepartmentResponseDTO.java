package ages.hopeful.modules.departments.dto;

import java.util.UUID;

import ages.hopeful.modules.departments.model.Department;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentResponseDTO {
    private UUID id;
    private String name;

    public static DepartmentResponseDTO fromModel(Department department) {
        if (department == null) return null;
        return new DepartmentResponseDTO(department.getId(), department.getName());
    }
    
}
