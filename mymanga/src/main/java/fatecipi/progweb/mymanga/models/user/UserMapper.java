package fatecipi.progweb.mymanga.models.user;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    void mapUser(UserCreateDto userCreateDto, @MappingTarget Users user);
}
