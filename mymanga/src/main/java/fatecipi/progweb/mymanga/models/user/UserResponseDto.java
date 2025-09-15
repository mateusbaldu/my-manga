package fatecipi.progweb.mymanga.models.user;

public record UserResponseDto(String name,
                              String email,
                              Role roles,
                              Adress adress) {
}
