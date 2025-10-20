package fatecipi.progweb.mymanga.models.dto.order;

import fatecipi.progweb.mymanga.models.enums.PaymentMethod;
import lombok.Builder;

import java.util.List;

@Builder
public record OrderCreate(PaymentMethod paymentMethod,
                          List<OrderItemsCreate> items) {
}
