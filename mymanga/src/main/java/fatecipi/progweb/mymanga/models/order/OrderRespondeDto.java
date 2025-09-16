package fatecipi.progweb.mymanga.models.order;

import fatecipi.progweb.mymanga.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderRespondeDto(Long id,
                               Instant createdAt,
                               BigDecimal finalPrice,
                               OrderStatus status,
                               List<OrderItemsResponseDto> items) {
}
