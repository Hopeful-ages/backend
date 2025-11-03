package ages.hopeful.factories;

import ages.hopeful.modules.user.model.User;

import java.util.UUID;

/**
 * Factory para criar instâncias de User para testes.
 */
public class UserFactory {

    private static int counter = 1;

    public static User createUser() {
        return createUser("user" + counter++ + "@test.com", "User Test", "USER");
    }

    public static User createUser(String email, String name, String roleName) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setCpf(generateCpf());
        user.setPhone("(51) 99999-9999");
        user.setPassword("$2a$10$cfFYnikRgfxgkq6I44oxeORd6Ud0PbK79OkLY2gv6URUEAdudGA9."); // senha: "password123"
        user.setAccountStatus(true);
        
        if ("ADMIN".equals(roleName)) {
            user.setRole(RoleFactory.createAdminRole());
        } else {
            user.setRole(RoleFactory.createUserRole());
        }
        
        return user;
    }

    public static User createUserWithId(UUID id, String email, String name) {
        User user = createUser(email, name, "USER");
        user.setId(id);
        return user;
    }

    public static User createAdmin() {
        return createUser("admin@test.com", "Admin Test", "ADMIN");
    }

    public static User createAdmin(String email, String name) {
        return createUser(email, name, "ADMIN");
    }

    public static User createInactiveUser() {
        User user = createUser();
        user.setAccountStatus(false);
        return user;
    }

    public static User createUserWithDepartmentAndCity() {
        User user = createUser();
        user.setDepartment(DepartmentFactory.createDefesaCivil());
        user.setCity(CityFactory.createCity());
        return user;
    }

    public static User createCompleteUser(String email, String name, String cpf) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setCpf(cpf);
        user.setPhone("(51) 98765-4321");
        user.setPassword("$2a$10$cfFYnikRgfxgkq6I44oxeORd6Ud0PbK79OkLY2gv6URUEAdudGA9.");
        user.setAccountStatus(true);
        user.setRole(RoleFactory.createUserRole());
        user.setDepartment(DepartmentFactory.createDefesaCivil());
        user.setCity(CityFactory.createCity());
        return user;
    }

    private static String generateCpf() {
        long cpfNumber = System.nanoTime() % 100000000000L;
        return String.format("%011d", cpfNumber);
    }
}
