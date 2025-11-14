package fatecipi.progweb.mymanga.dto.address;

public record AddressResponse(
        Long id,
        String cep,
        String street,
        String number,
        String complement,
        String locality,
        String city,
        String state
){
}
