package fatecipi.progweb.mymanga.models.dto.security;

public record LoginResponse(String accessToken, Long expiresIn) {
}
