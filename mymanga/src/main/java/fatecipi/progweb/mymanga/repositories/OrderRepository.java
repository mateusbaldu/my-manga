package fatecipi.progweb.mymanga.repositories;

import fatecipi.progweb.mymanga.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUsers_Username(String email, Pageable pageable);
    Optional<Order> findByConfirmationToken(String token);
    Page<Order> findByUsers_Id(Long id, Pageable pageable);
}
