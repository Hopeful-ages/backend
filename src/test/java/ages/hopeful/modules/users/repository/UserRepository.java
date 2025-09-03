package ages.hopeful.modules.users.repository;

import ages.hopeful.modules.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, java.util.UUID> {
    List<User> findByStatus(boolean status);
}

