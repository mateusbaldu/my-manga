package fatecipi.progweb.mymanga.listeners;

import fatecipi.progweb.mymanga.models.AuditLog;
import fatecipi.progweb.mymanga.models.enums.AuditAction;
import fatecipi.progweb.mymanga.models.enums.AuditStatus;
import fatecipi.progweb.mymanga.repositories.AuditLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuditEventListenerTest {
    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditEventListener auditEventListener;

    @Test
    @DisplayName("onAuditEvent should persist AuditLog in repository")
    void onAuditEvent_ShouldSaveAuditLog() {
        AuditEvent event = new AuditEvent(
                AuditAction.ORDER_CREATED,
                "Order",
                "42",
                5L,
                "usertest",
                "192.168.1.10",
                AuditStatus.SUCCESS,
                "ORDER_CREATED executed on Order (id=42)"
        );

        auditEventListener.onAuditEvent(event);

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository, times(1)).save(captor.capture());

        AuditLog saved = captor.getValue();
        assertEquals(AuditAction.ORDER_CREATED, saved.getAction());
        assertEquals("Order", saved.getEntityType());
        assertEquals("42", saved.getEntityId());
        assertEquals(5L, saved.getActorId());
        assertEquals("usertest", saved.getActorUsername());
        assertEquals("192.168.1.10", saved.getIpAddress());
        assertEquals(AuditStatus.SUCCESS, saved.getStatus());
    }
}
