package fatecipi.progweb.mymanga.dto.address;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AddressUpdate (
        @NotNull(message = "Field can't be null")
        String cep,
        String street,
        String number,
        String complement,
        String locality,
        String city,
        String state
){
}
