package ages.hopeful.modules.users.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import ages.hopeful.modules.users.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {
  boolean existsByEmail(String email);
  boolean existsByCpf(String cpf);
}
