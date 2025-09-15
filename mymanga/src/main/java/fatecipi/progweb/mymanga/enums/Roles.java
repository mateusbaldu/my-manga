package fatecipi.progweb.mymanga.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Roles {
    BASIC(2L), SUBSCRIBER(3L), ADMIN(4L);

    private final Long id;
}
