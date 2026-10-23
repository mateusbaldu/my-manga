package fatecipi.progweb.mymanga.dto.audit;

import fatecipi.progweb.mymanga.models.enums.AuditAction;
import fatecipi.progweb.mymanga.models.enums.AuditStatus;

import java.time.Instant;

public record AuditLogResponse(
        Long id,
        AuditAction action,
        String entityType,
        String entityId,
        Long actorId,
        String actorUsername,
        String ipAddress,
        AuditStatus status,
        String details,
        Instant createdAt
) {
}
