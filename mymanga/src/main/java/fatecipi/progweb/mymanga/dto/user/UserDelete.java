package fatecipi.progweb.mymanga.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserDelete(
        @Email(message = "Invalid email")
        @NotNull(message = "Field can't be null")
        String email) {
}
