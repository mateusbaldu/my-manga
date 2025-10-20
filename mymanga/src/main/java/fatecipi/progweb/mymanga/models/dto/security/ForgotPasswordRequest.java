package fatecipi.progweb.mymanga.models.dto.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record ForgotPasswordRequest (
        @NotNull(message = "Field can't be null")
        @Email(message = "Invalid email")
        String email) {
}
