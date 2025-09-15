package fatecipi.progweb.mymanga.models.order;

import fatecipi.progweb.mymanga.enums.OrderStatus;
import fatecipi.progweb.mymanga.enums.PaymentMethod;
import fatecipi.progweb.mymanga.models.user.Users;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Table(name = "orders")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")

    private Long id;
    private Instant createdAt;
    private BigDecimal finalPrice;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.WAITING_CONFIRMATION;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItems> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;

}
