package fatecipi.progweb.mymanga.mappers;

import fatecipi.progweb.mymanga.dto.audit.AuditLogResponse;
import fatecipi.progweb.mymanga.models.AuditLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {
    AuditLogResponse toResponse(AuditLog auditLog);
}
