package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.dto.audit.AuditLogResponse;
import fatecipi.progweb.mymanga.dto.audit.AuditLogSearchFilter;
import fatecipi.progweb.mymanga.services.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Audit Logs", description = "Endpoints for consulting system audit logs (Admin only)")
@RestController
@RequestMapping("/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {
    private final AuditLogService auditLogService;

    @Operation(summary = "List and filter audit logs with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Audit logs retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Page<AuditLogResponse>> listAll(Pageable pageable, AuditLogSearchFilter filter) {
        return ResponseEntity.ok(auditLogService.listAll(pageable, filter));
    }

    @Operation(summary = "Get a specific audit log by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Audit log found successfully"),
            @ApiResponse(responseCode = "404", description = "Audit log not found"),
            @ApiResponse(responseCode = "403", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<AuditLogResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(auditLogService.getById(id));
    }
}
