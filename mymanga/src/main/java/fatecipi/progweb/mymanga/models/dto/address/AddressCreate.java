package fatecipi.progweb.mymanga.models.dto.address;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record AddressCreate(@NotEmpty(message = "Cep can't be empty")
                            String cep,
                            String number,
                            String complement) {
}
