package fatecipi.progweb.mymanga.listeners;

import fatecipi.progweb.mymanga.models.Order;

public record OrderCreatedEvent (Order order) {
}
