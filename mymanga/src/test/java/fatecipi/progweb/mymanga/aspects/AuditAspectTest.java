package fatecipi.progweb.mymanga.aspects;

import fatecipi.progweb.mymanga.annotations.Auditable;
import fatecipi.progweb.mymanga.dto.security.LoginRequest;
import fatecipi.progweb.mymanga.exceptions.InvalidLoginException;
import fatecipi.progweb.mymanga.listeners.AuditEvent;
import fatecipi.progweb.mymanga.models.enums.AuditAction;
import fatecipi.progweb.mymanga.models.enums.AuditStatus;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.lang.annotation.Annotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditAspectTest {
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @InjectMocks
    private AuditAspect auditAspect;

    private Auditable createAuditable(AuditAction action, String entityType) {
        return new Auditable() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Auditable.class;
            }

            @Override
            public AuditAction action() {
                return action;
            }

            @Override
            public String entityType() {
                return entityType;
            }
        };
    }

    @Test
    @DisplayName("auditMethod should publish SUCCESS AuditEvent when method succeeds")
    void auditMethod_ShouldPublishSuccessEvent_WhenMethodSucceeds() throws Throwable {
        Auditable auditable = createAuditable(AuditAction.MANGA_DELETED, "Manga");
        when(joinPoint.getArgs()).thenReturn(new Object[]{15L});
        when(joinPoint.proceed()).thenReturn(null);

        auditAspect.auditMethod(joinPoint, auditable);

        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(eventPublisher, times(1)).publishEvent(captor.capture());

        AuditEvent event = captor.getValue();
        assertEquals(AuditAction.MANGA_DELETED, event.action());
        assertEquals("Manga", event.entityType());
        assertEquals("15", event.entityId());
        assertEquals(AuditStatus.SUCCESS, event.status());
    }

    @Test
    @DisplayName("auditMethod should publish USER_LOGIN_FAILED with FAILURE status when login throws exception")
    void auditMethod_ShouldPublishFailureEvent_WhenLoginFails() throws Throwable {
        Auditable auditable = createAuditable(AuditAction.USER_LOGIN, "Auth");
        LoginRequest loginRequest = new LoginRequest("admin@mymanga.com", "wrongpass");
        when(joinPoint.getArgs()).thenReturn(new Object[]{loginRequest});
        when(joinPoint.proceed()).thenThrow(new InvalidLoginException("Invalid password"));

        assertThrows(InvalidLoginException.class, () -> auditAspect.auditMethod(joinPoint, auditable));

        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(eventPublisher, times(1)).publishEvent(captor.capture());

        AuditEvent event = captor.getValue();
        assertEquals(AuditAction.USER_LOGIN_FAILED, event.action());
        assertEquals("Auth", event.entityType());
        assertEquals("admin@mymanga.com", event.actorUsername());
        assertEquals(AuditStatus.FAILURE, event.status());
    }
}
