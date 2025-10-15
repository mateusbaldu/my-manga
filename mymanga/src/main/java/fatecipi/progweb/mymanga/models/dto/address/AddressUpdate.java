package fatecipi.progweb.mymanga.models.dto.address;

public record AddressUpdate (
        String cep,
        String street,
        String number,
        String complement,
        String locality,
        String city,
        String state
){
}
