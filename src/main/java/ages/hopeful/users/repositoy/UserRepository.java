package ages.hopeful.users.repositoy;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import ages.hopeful.users.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
  boolean existsByEmail(String email);
  boolean existsByCpf(String cpf);
}
