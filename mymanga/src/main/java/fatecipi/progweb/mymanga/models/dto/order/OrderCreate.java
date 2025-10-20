package fatecipi.progweb.mymanga.models.dto.order;

import fatecipi.progweb.mymanga.models.enums.PaymentMethod;

import java.util.List;

public record OrderCreate(PaymentMethod paymentMethod,
                          List<OrderItemsCreate> items) {
}
