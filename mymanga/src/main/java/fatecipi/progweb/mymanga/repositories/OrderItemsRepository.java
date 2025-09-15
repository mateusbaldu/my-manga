package fatecipi.progweb.mymanga.repositories;

import fatecipi.progweb.mymanga.models.order.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItems, Long> {
}
