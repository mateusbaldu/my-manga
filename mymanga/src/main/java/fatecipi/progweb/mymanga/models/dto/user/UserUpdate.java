package fatecipi.progweb.mymanga.models.dto.user;

import lombok.Builder;

@Builder
public record UserUpdate(String name,
                         String email,
                         String username) {
}
