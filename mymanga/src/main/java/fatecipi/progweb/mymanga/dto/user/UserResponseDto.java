package fatecipi.progweb.mymanga.dto.user;

import fatecipi.progweb.mymanga.models.user.Adress;
import fatecipi.progweb.mymanga.models.user.Role;
import lombok.Builder;

import java.time.Instant;
import java.util.Set;

@Builder
public record UserResponseDto(String name,
                              String email,
                              Instant createdAt,
                              Set<Role> roles,
                              Adress adress) {
}
