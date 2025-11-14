package fatecipi.progweb.mymanga.dto.user;

import lombok.Builder;

@Builder
public record UserUpdate(String name,
                         String email,
                         String username) {
}
