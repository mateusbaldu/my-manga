package fatecipi.progweb.mymanga.models.dto.security;

public record ResetPasswordRequest(String token, String newPassword) {
}
