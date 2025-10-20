package fatecipi.progweb.mymanga.models.dto.order;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record OrderItemsCreate(
        @NotNull(message = "Field can't be null")
        Long volumeId,
        @NotNull(message = "Field can't be null")
        Integer quantity) {
}
