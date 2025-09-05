package ages.hopeful.modules.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import ages.hopeful.modules.user.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    List<User> findAllByOrderByNameAsc();
    List<User> findByAccountStatusOrderByNameAsc(Boolean filter);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    Optional<User> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.accountStatus = false WHERE u.id = :id")
    void disableUserById(@Param("id") UUID id);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.accountStatus = true WHERE u.id = :id")
    void enableUserById(@Param("id") UUID id);

}
