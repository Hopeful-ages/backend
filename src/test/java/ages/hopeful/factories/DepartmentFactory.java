package ages.hopeful.factories;

import java.util.UUID;

import ages.hopeful.modules.departments.model.Department;

/**
 * Factory para criar instâncias de Department (Service) para testes.
 */
public class DepartmentFactory {

    public static Department createDepartment() {
        return createDepartment("Defesa Civil");
    }

    public static Department createDepartment(String name) {
        Department department = new Department();
        department.setName(name);
        return department;
    }

    public static Department createDepartmentWithId(UUID id, String name) {
        Department department = createDepartment(name);
        department.setId(id);
        return department;
    }

    public static Department createDefesaCivil() {
        return createDepartment("Defesa Civil");
    }

    public static Department createBombeiros() {
        return createDepartment("Corpo de Bombeiros");
    }

    public static Department createSamu() {
        return createDepartment("SAMU - Emergência");
    }

    public static Department createObras() {
        return createDepartment("Secretaria de Obras");
    }
}
