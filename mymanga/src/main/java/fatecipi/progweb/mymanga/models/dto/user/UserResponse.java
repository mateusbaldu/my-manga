package fatecipi.progweb.mymanga.models.dto.user;

import fatecipi.progweb.mymanga.models.Role;
import lombok.Builder;

import java.time.Instant;
import java.util.Set;

@Builder
public record UserResponse(String name,
                           String username,
                           Instant createdAt,
                           Set<Role> roles) {
}
