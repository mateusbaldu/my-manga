package fatecipi.progweb.mymanga.dto.user;

import fatecipi.progweb.mymanga.models.user.Adress;

public record UserCreateDto(String email,
                            String name,
                            String password,
                            Adress adress) {
}
