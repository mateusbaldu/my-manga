package fatecipi.progweb.mymanga.models.order;

import fatecipi.progweb.mymanga.enums.OrderStatus;
import fatecipi.progweb.mymanga.enums.PaymentMethod;
import fatecipi.progweb.mymanga.models.user.Users;

import java.math.BigDecimal;
import java.util.List;

public record OrderCreateDto(String email,
                             PaymentMethod paymentMethod,
                             List<OrderItemsCreateDto> items) {
}
