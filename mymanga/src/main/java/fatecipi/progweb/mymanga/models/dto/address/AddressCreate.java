package fatecipi.progweb.mymanga.models.dto.address;

import jakarta.validation.constraints.NotNull;

public record AddressCreate(@NotNull(message = "Field can't be null")
                            String cep,
                            String number,
                            String complement) {
}
