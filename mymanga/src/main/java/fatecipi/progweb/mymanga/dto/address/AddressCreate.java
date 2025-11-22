package fatecipi.progweb.mymanga.dto.address;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

//TODO: documentar DTOs

@Builder
public record AddressCreate(@NotEmpty(message = "Cep can't be empty")
                            @Size(min = 8, max = 8)
                            String cep,
                            String number,
                            String complement) {
}
