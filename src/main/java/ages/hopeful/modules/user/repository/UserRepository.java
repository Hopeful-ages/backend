package ages.hopeful.modules.user.repository;

import ages.hopeful.modules.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    Optional<User> findByEmail(String email);
    List<User> findAllByOrderByNameAsc();
    List<User> findByAccountStatusOrderByNameAsc(Boolean filter);
}
