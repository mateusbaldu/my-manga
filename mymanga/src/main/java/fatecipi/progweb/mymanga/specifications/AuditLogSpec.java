package fatecipi.progweb.mymanga.specifications;

import fatecipi.progweb.mymanga.dto.audit.AuditLogSearchFilter;
import fatecipi.progweb.mymanga.models.AuditLog;
import fatecipi.progweb.mymanga.models.enums.AuditAction;
import fatecipi.progweb.mymanga.models.enums.AuditStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class AuditLogSpec {
    private static boolean isInvalid(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static Specification<AuditLog> filterByAction(AuditAction action) {
        return (root, query, builder) -> {
            if (action == null) {
                return null;
            }
            return builder.equal(root.get("action"), action);
        };
    }

    private static Specification<AuditLog> filterByEntityType(String entityType) {
        return (root, query, builder) -> {
            if (isInvalid(entityType)) {
                return null;
            }
            return builder.equal(builder.lower(root.get("entityType")), entityType.trim().toLowerCase());
        };
    }

    private static Specification<AuditLog> filterByActorUsername(String actorUsername) {
        return (root, query, builder) -> {
            if (isInvalid(actorUsername)) {
                return null;
            }
            return builder.like(builder.lower(root.get("actorUsername")), "%" + actorUsername.trim().toLowerCase() + "%");
        };
    }

    private static Specification<AuditLog> filterByStatus(AuditStatus status) {
        return (root, query, builder) -> {
            if (status == null) {
                return null;
            }
            return builder.equal(root.get("status"), status);
        };
    }

    private static Specification<AuditLog> filterByStartDate(Instant startDate) {
        return (root, query, builder) -> {
            if (startDate == null) {
                return null;
            }
            return builder.greaterThanOrEqualTo(root.get("createdAt"), startDate);
        };
    }

    private static Specification<AuditLog> filterByEndDate(Instant endDate) {
        return (root, query, builder) -> {
            if (endDate == null) {
                return null;
            }
            return builder.lessThanOrEqualTo(root.get("createdAt"), endDate);
        };
    }

    public static Specification<AuditLog> filter(AuditLogSearchFilter filter) {
        if (filter == null) {
            return Specification.unrestricted();
        }

        return Specification.<AuditLog>unrestricted()
                .and(filterByAction(filter.action()))
                .and(filterByEntityType(filter.entityType()))
                .and(filterByActorUsername(filter.actorUsername()))
                .and(filterByStatus(filter.status()))
                .and(filterByStartDate(filter.startDate()))
                .and(filterByEndDate(filter.endDate()));
    }
}
