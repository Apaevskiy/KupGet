package kup.get.repository.postgres.security;


import kup.get.entity.postgres.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleRepository extends JpaRepository<Role, Long> {
}
