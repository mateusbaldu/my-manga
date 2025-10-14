package fatecipi.progweb.mymanga.models.dto.user;

import fatecipi.progweb.mymanga.models.Address;

public record UserUpdate(String name,
                         String email,
                         String username) {
}
