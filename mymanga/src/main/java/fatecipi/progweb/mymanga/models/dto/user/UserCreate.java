package fatecipi.progweb.mymanga.models.dto.user;

import fatecipi.progweb.mymanga.models.Adress;

public record UserCreate(String name,
                         String email,
                         String username,
                         String password,
                         Adress adress) {
}
