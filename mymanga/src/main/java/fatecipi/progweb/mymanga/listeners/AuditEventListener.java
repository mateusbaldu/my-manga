package fatecipi.progweb.mymanga.listeners;

import fatecipi.progweb.mymanga.models.AuditLog;
import fatecipi.progweb.mymanga.models.enums.AuditStatus;
import fatecipi.progweb.mymanga.repositories.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AuditEventListener {
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT_LOGGER");

    private final AuditLogRepository auditLogRepository;

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onAuditEvent(AuditEvent event) {
        AuditLog auditLog = AuditLog.builder()
                .action(event.action())
                .entityType(event.entityType())
                .entityId(event.entityId())
                .actorId(event.actorId())
                .actorUsername(event.actorUsername())
                .ipAddress(event.ipAddress())
                .status(event.status())
                .details(event.details())
                .build();

        auditLogRepository.save(auditLog);

        String logMessage = String.format(
                "action=%s | status=%s | entityType=%s | entityId=%s | actorId=%s | actorUsername=%s | ip=%s | details=%s",
                event.action(),
                event.status(),
                event.entityType(),
                event.entityId() != null ? event.entityId() : "N/A",
                event.actorId() != null ? event.actorId() : "N/A",
                event.actorUsername() != null ? event.actorUsername() : "ANONYMOUS",
                event.ipAddress() != null ? event.ipAddress() : "UNKNOWN",
                event.details() != null ? event.details() : ""
        );

        if (event.status() == AuditStatus.FAILURE) {
            auditLogger.warn(logMessage);
        } else {
            auditLogger.info(logMessage);
        }
    }
}
