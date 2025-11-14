package fatecipi.progweb.mymanga.dto.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record ResetPasswordRequest(String token,
                                   @NotNull(message = "Field can't be null")
                                   String newPassword) {
}
