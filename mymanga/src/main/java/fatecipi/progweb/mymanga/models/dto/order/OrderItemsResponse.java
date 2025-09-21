package fatecipi.progweb.mymanga.models.dto.order;

import java.math.BigDecimal;

public record OrderItemsResponse(String mangaTitle,
                                 Integer volumeNumber,
                                 Integer quantity,
                                 BigDecimal unitPrice) {
}
