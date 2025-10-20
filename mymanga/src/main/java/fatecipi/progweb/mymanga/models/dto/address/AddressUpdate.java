package fatecipi.progweb.mymanga.models.dto.address;

import jakarta.validation.constraints.NotNull;

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
