package fatecipi.progweb.mymanga.dto.order;

import fatecipi.progweb.mymanga.enums.OrderStatus;
import fatecipi.progweb.mymanga.enums.PaymentMethod;
import fatecipi.progweb.mymanga.models.user.Users;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponseDto(Long id,
                               Instant createdAt,
                               BigDecimal finalPrice,
                               PaymentMethod paymentMethod,
                               OrderStatus status,
                               List<OrderItemsResponseDto> items,
                               Users user){
}
