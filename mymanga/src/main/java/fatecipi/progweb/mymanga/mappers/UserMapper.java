package fatecipi.progweb.mymanga.mappers;

import fatecipi.progweb.mymanga.dto.user.UserCreate;
import fatecipi.progweb.mymanga.dto.user.UserResponse;
import fatecipi.progweb.mymanga.dto.user.UserUpdate;
import fatecipi.progweb.mymanga.models.Users;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    void createMapping(UserCreate userCreate, @MappingTarget Users user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateMapping(UserUpdate userUpdate, @MappingTarget Users user);

    @Mapping(source = "roles", target = "roles")
    UserResponse responseMapping(Users user);
}
