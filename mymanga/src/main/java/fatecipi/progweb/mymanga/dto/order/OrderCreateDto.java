package fatecipi.progweb.mymanga.dto.order;

import fatecipi.progweb.mymanga.enums.PaymentMethod;

import java.util.List;

public record OrderCreateDto(String email,
                             PaymentMethod paymentMethod,
                             List<OrderItemsCreateDto> items) {
}
