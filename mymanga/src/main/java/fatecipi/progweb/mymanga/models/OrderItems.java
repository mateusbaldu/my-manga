package fatecipi.progweb.mymanga.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Table(name = "order_items")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mangaTitle;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order orders;
}
