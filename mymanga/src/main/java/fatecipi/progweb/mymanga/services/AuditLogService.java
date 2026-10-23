package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.dto.audit.AuditLogResponse;
import fatecipi.progweb.mymanga.dto.audit.AuditLogSearchFilter;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.mappers.AuditLogMapper;
import fatecipi.progweb.mymanga.models.AuditLog;
import fatecipi.progweb.mymanga.repositories.AuditLogRepository;
import fatecipi.progweb.mymanga.specifications.AuditLogSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> listAll(Pageable pageable, AuditLogSearchFilter filter) {
        return auditLogRepository.findAll(AuditLogSpec.filter(filter), pageable)
                .map(auditLogMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public AuditLogResponse getById(Long id) {
        AuditLog auditLog = auditLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AuditLog with id " + id + " was not found"));
        return auditLogMapper.toResponse(auditLog);
    }
}
