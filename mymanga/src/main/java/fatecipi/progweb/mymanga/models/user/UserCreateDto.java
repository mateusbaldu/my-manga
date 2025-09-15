package fatecipi.progweb.mymanga.models.user;

public record UserCreateDto(String email,
                            String name,
                            String password,
                            Adress adress,
                            Role role) {
}
