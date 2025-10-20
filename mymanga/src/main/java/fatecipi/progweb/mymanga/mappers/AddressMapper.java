package fatecipi.progweb.mymanga.mappers;

import fatecipi.progweb.mymanga.models.Address;
import fatecipi.progweb.mymanga.models.dto.address.AddressResponse;
import fatecipi.progweb.mymanga.models.dto.address.AddressUpdate;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressMapper {
    @Mapping(source = "cep", target = "cep")
    AddressResponse toAddressResponse(Address address);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void mapAddress(AddressUpdate addressUpdate, @MappingTarget Address address);
}
