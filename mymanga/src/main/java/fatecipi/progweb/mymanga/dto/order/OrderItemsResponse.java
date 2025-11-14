package fatecipi.progweb.mymanga.dto.order;

import java.math.BigDecimal;

public record OrderItemsResponse(String mangaTitle,
                                 Integer volumeNumber,
                                 Integer quantity,
                                 BigDecimal unitPrice) {
}
