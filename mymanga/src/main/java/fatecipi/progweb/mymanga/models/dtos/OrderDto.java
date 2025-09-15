package fatecipi.progweb.mymanga.models.dtos;

import fatecipi.progweb.mymanga.enums.OrderStatus;
import fatecipi.progweb.mymanga.enums.PaymentMethod;
import fatecipi.progweb.mymanga.models.OrderItems;
import fatecipi.progweb.mymanga.models.Users;

import java.math.BigDecimal;
import java.util.List;

public record OrderDto (BigDecimal finalPrice,
                        PaymentMethod paymentMethod,
                        OrderStatus status,
                        List<OrderItems> items,
                        Users users) {
}
