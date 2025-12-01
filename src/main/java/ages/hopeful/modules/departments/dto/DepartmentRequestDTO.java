package ages.hopeful.modules.departments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ages.hopeful.modules.departments.model.Department;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class DepartmentRequestDTO {
    @NotBlank @Size(max = 150)
    private String name;

    public Department toModel() {
        Department department = new Department();
        department.setName(this.name);
        return department;
    }
}

