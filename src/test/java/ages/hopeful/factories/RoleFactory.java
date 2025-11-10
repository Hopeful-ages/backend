package ages.hopeful.factories;

import ages.hopeful.modules.user.model.Role;

import java.util.UUID;

/**
 * Factory para criar instâncias de Role para testes.
 */
public class RoleFactory {

    public static final UUID ROLE_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440003");
    public static final UUID ROLE_ADMIN_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440004");

    public static Role createUserRole() {
        Role role = new Role();
        role.setId(ROLE_USER_ID);
        role.setName("USER");
        return role;
    }

    public static Role createAdminRole() {
        Role role = new Role();
        role.setId(ROLE_ADMIN_ID);
        role.setName("ADMIN");
        return role;
    }

    public static Role createRole(String name) {
        Role role = new Role();
        role.setName(name);
        return role;
    }

    public static Role createRoleWithId(UUID id, String name) {
        Role role = new Role();
        role.setId(id);
        role.setName(name);
        return role;
    }
}
