package fatecipi.progweb.mymanga.models.mappers;

import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.models.dtos.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    void mapUser(UserDto userDto, @MappingTarget Users user);
}
