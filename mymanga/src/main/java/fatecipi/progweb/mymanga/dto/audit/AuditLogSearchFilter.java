package fatecipi.progweb.mymanga.dto.audit;

import fatecipi.progweb.mymanga.models.enums.AuditAction;
import fatecipi.progweb.mymanga.models.enums.AuditStatus;

import java.time.Instant;

public record AuditLogSearchFilter(
        AuditAction action,
        String entityType,
        String actorUsername,
        AuditStatus status,
        Instant startDate,
        Instant endDate
) {
}
