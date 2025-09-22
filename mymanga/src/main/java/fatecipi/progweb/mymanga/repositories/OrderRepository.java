package fatecipi.progweb.mymanga.repositories;

import fatecipi.progweb.mymanga.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUsers_Email(String email);
    Optional<Order> findByConfirmationToken(String token);
}
