package fatecipi.progweb.mymanga.models.user;

import fatecipi.progweb.mymanga.dto.user.UserCreateDto;
import fatecipi.progweb.mymanga.dto.user.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    void mapCreateUser(UserCreateDto userCreateDto, @MappingTarget Users user);
    @Mapping(source = "role", target = "roles")
    UserResponseDto toUserResponseDto(Users user);
}
