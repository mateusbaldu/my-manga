package fatecipi.progweb.mymanga.configs.mappers;

import fatecipi.progweb.mymanga.models.dto.user.UserCreate;
import fatecipi.progweb.mymanga.models.dto.user.UserResponse;
import fatecipi.progweb.mymanga.models.dto.user.UserUpdate;
import fatecipi.progweb.mymanga.models.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    void mapCreateUser(UserCreate userCreate, @MappingTarget Users user);
    void mapUpdateUser(UserUpdate userUpdate, @MappingTarget Users user);
    @Mapping(source = "roles", target = "roles")
    UserResponse toUserResponse(Users user);
}
