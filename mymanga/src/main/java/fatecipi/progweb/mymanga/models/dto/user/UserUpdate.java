package fatecipi.progweb.mymanga.models.dto.user;

import fatecipi.progweb.mymanga.models.Adress;

public record UserUpdate(String name,
                         String email,
                         String username,
                         Adress adress) {
}
