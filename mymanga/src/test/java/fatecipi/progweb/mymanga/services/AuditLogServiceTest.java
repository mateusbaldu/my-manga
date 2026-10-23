package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.dto.audit.AuditLogResponse;
import fatecipi.progweb.mymanga.dto.audit.AuditLogSearchFilter;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.mappers.AuditLogMapper;
import fatecipi.progweb.mymanga.models.AuditLog;
import fatecipi.progweb.mymanga.models.enums.AuditAction;
import fatecipi.progweb.mymanga.models.enums.AuditStatus;
import fatecipi.progweb.mymanga.repositories.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {
    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private AuditLogMapper auditLogMapper;

    @InjectMocks
    private AuditLogService auditLogService;

    private AuditLog auditLog;
    private AuditLogResponse auditLogResponse;

    @BeforeEach
    void setUp() {
        Instant now = Instant.now();
        auditLog = AuditLog.builder()
                .id(1L)
                .action(AuditAction.MANGA_CREATED)
                .entityType("Manga")
                .entityId("10")
                .actorId(1L)
                .actorUsername("admin")
                .ipAddress("127.0.0.1")
                .status(AuditStatus.SUCCESS)
                .details("MANGA_CREATED executed on Manga (id=10, title='One Piece')")
                .createdAt(now)
                .build();

        auditLogResponse = new AuditLogResponse(
                1L,
                AuditAction.MANGA_CREATED,
                "Manga",
                "10",
                1L,
                "admin",
                "127.0.0.1",
                AuditStatus.SUCCESS,
                "MANGA_CREATED executed on Manga (id=10, title='One Piece')",
                now
        );
    }

    @Test
    @DisplayName("listAll should return paged AuditLogResponse")
    @SuppressWarnings("unchecked")
    void listAll_ShouldReturnPagedAuditLogResponse() {
        Pageable pageable = PageRequest.of(0, 10);
        AuditLogSearchFilter filter = new AuditLogSearchFilter(
                AuditAction.MANGA_CREATED, "Manga", "admin", AuditStatus.SUCCESS, null, null
        );
        Page<AuditLog> page = new PageImpl<>(List.of(auditLog), pageable, 1);

        when(auditLogRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(auditLogMapper.toResponse(auditLog)).thenReturn(auditLogResponse);

        Page<AuditLogResponse> result = auditLogService.listAll(pageable, filter);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(AuditAction.MANGA_CREATED, result.getContent().getFirst().action());
        verify(auditLogRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("getById should return AuditLogResponse when id exists")
    void getById_ShouldReturnAuditLogResponse_WhenExists() {
        when(auditLogRepository.findById(1L)).thenReturn(Optional.of(auditLog));
        when(auditLogMapper.toResponse(auditLog)).thenReturn(auditLogResponse);

        AuditLogResponse result = auditLogService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("admin", result.actorUsername());
        verify(auditLogRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getById should throw ResourceNotFoundException when id does not exist")
    void getById_ShouldThrowResourceNotFoundException_WhenNotFound() {
        when(auditLogRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> auditLogService.getById(99L));
        verify(auditLogRepository, times(1)).findById(99L);
    }
}
