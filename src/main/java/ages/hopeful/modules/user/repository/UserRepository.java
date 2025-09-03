package ages.hopeful.modules.user.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import ages.hopeful.modules.user.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {
  boolean existsByEmail(String email);

  boolean existsByCpf(String cpf);

  Optional<User> findByEmail(String email);

  // Listing helpers
  java.util.List<User> findAllByOrderByNameAsc();

  java.util.List<User> findByAccountStatusOrderByNameAsc(boolean accountStatus);

}
