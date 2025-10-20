package fatecipi.progweb.mymanga.models.dto.user;

import fatecipi.progweb.mymanga.configs.validation.ValidUsername;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserCreate(
        @NotNull(message = "Field can't be null")
        String name,
        @Email(message = "Invalid email")
        @Column(unique = true)
        @NotNull(message = "Field can't be null")
        String email,
        @ValidUsername(message = "Invalid username")
        @NotNull(message = "Field can't be null")
        String username,
        @NotNull(message = "Field can't be null")
        String password) {
}
