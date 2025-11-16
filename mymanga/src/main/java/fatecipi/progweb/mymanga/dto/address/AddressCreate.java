package fatecipi.progweb.mymanga.dto.address;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

//TODO: documentar DTOs

@Builder
public record AddressCreate(@NotEmpty(message = "Cep can't be empty")
                            String cep,
                            String number,
                            String complement) {
}
