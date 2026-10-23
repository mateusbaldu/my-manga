package fatecipi.progweb.mymanga.listeners;

import fatecipi.progweb.mymanga.models.enums.AuditAction;
import fatecipi.progweb.mymanga.models.enums.AuditStatus;

public record AuditEvent(
        AuditAction action,
        String entityType,
        String entityId,
        Long actorId,
        String actorUsername,
        String ipAddress,
        AuditStatus status,
        String details
) {
}
