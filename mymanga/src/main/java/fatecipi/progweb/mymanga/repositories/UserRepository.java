package fatecipi.progweb.mymanga.repositories;

import fatecipi.progweb.mymanga.models.Users;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {
    Optional<Users> findByEmail(String name);
    boolean existsByEmail(String email);
}
