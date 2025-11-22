package fatecipi.progweb.mymanga.listeners;

import fatecipi.progweb.mymanga.models.Users;

public record PasswordResetRequestedEvent(Users user) {
}
