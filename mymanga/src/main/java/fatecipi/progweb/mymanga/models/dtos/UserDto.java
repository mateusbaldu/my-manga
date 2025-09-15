package fatecipi.progweb.mymanga.models.dtos;

import fatecipi.progweb.mymanga.models.Adress;
import fatecipi.progweb.mymanga.models.Role;

public record UserDto(String email,
                      String name,
                      String password,
                      Adress adress,
                      Role role) {
}
