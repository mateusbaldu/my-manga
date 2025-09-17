package fatecipi.progweb.mymanga.dto.user;

import fatecipi.progweb.mymanga.models.user.Adress;

public record UserUpdateDto(String name,
                            Adress adress) {
}
