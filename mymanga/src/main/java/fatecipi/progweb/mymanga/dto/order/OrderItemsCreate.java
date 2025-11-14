package fatecipi.progweb.mymanga.dto.order;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OrderItemsCreate(
        @NotNull(message = "Field can't be null")
        Long volumeId,
        @NotNull(message = "Field can't be null")
        Integer quantity) {
}
