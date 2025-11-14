package fatecipi.progweb.mymanga.dto.order;

import fatecipi.progweb.mymanga.models.enums.OrderStatus;
import fatecipi.progweb.mymanga.models.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(Long id,
                            Instant createdAt,
                            BigDecimal finalPrice,
                            PaymentMethod paymentMethod,
                            OrderStatus status,
                            List<OrderItemsResponse> items,
                            String username
){
}
