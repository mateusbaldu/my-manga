package fatecipi.progweb.mymanga.repositories;

import fatecipi.progweb.mymanga.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String name);
    Optional<Users> findByUsername(String username);
    boolean existsByUsername(String username);
}
