package ages.hopeful.modules.departments.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ages.hopeful.modules.departments.model.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
}