package fatecipi.progweb.mymanga.aspects;

import fatecipi.progweb.mymanga.annotations.Auditable;
import fatecipi.progweb.mymanga.dto.address.AddressResponse;
import fatecipi.progweb.mymanga.dto.manga.MangaCreate;
import fatecipi.progweb.mymanga.dto.manga.MangaResponse;
import fatecipi.progweb.mymanga.dto.order.OrderResponse;
import fatecipi.progweb.mymanga.dto.security.LoginRequest;
import fatecipi.progweb.mymanga.dto.user.UserCreate;
import fatecipi.progweb.mymanga.dto.user.UserResponse;
import fatecipi.progweb.mymanga.dto.volume.VolumeResponse;
import fatecipi.progweb.mymanga.listeners.AuditEvent;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.models.enums.AuditAction;
import fatecipi.progweb.mymanga.models.enums.AuditStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {
    private final ApplicationEventPublisher eventPublisher;

    @Around("@annotation(auditable)")
    public Object auditMethod(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        Object[] args = joinPoint.getArgs();
        ActorInfo actor = resolveActor(args);
        String ipAddress = resolveIpAddress();

        try {
            Object result = joinPoint.proceed();
            String entityId = resolveEntityId(result, args);
            String details = buildSuccessDetails(auditable.action(), auditable.entityType(), entityId, result);

            eventPublisher.publishEvent(new AuditEvent(
                    auditable.action(),
                    auditable.entityType(),
                    entityId,
                    actor.actorId(),
                    actor.actorUsername(),
                    ipAddress,
                    AuditStatus.SUCCESS,
                    details
            ));
            return result;
        } catch (Throwable ex) {
            AuditAction action = auditable.action() == AuditAction.USER_LOGIN
                    ? AuditAction.USER_LOGIN_FAILED
                    : auditable.action();
            String entityId = resolveEntityId(null, args);
            String details = "Operation failed: " + ex.getClass().getSimpleName()
                    + (ex.getMessage() != null ? " - " + ex.getMessage() : "");

            eventPublisher.publishEvent(new AuditEvent(
                    action,
                    auditable.entityType(),
                    entityId,
                    actor.actorId(),
                    actor.actorUsername(),
                    ipAddress,
                    AuditStatus.FAILURE,
                    details
            ));
            throw ex;
        }
    }

    private ActorInfo resolveActor(Object[] args) {
        Long actorId = null;
        String actorUsername = null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            try {
                actorId = Long.valueOf(jwt.getSubject());
            } catch (NumberFormatException ignored) {
            }
            actorUsername = jwt.getClaimAsString("username");
        } else if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            try {
                actorId = Long.valueOf(auth.getName());
            } catch (NumberFormatException ignored) {
                actorUsername = auth.getName();
            }
        }

        if (args != null) {
            for (Object arg : args) {
                if (arg instanceof Users user) {
                    if (actorId == null) actorId = user.getId();
                    if (actorUsername == null) actorUsername = user.getUsername();
                } else if (arg instanceof LoginRequest loginReq && actorUsername == null) {
                    actorUsername = loginReq.email();
                } else if (arg instanceof UserCreate userCreate && actorUsername == null) {
                    actorUsername = userCreate.username();
                }
            }
        }

        if (actorUsername == null && args != null && args.length > 0 && args[0] instanceof String strArg) {
            // Para métodos como addNewAddressToUser(String username, ...) ou requestPasswordReset(String email)
            // Evita usar tokens UUID como username
            if (!isUuidToken(strArg)) {
                actorUsername = strArg;
            }
        }

        return new ActorInfo(actorId, actorUsername != null ? actorUsername : "ANONYMOUS");
    }

    private String resolveEntityId(Object result, Object[] args) {
        if (result instanceof MangaResponse r && r.id() != null) return r.id().toString();
        if (result instanceof VolumeResponse r && r.id() != null) return r.id().toString();
        if (result instanceof OrderResponse r && r.id() != null) return r.id().toString();
        if (result instanceof AddressResponse r && r.id() != null) return r.id().toString();
        if (result instanceof UserResponse r && r.username() != null) return r.username();

        if (args != null) {
            Long lastLongArg = null;
            for (Object arg : args) {
                if (arg instanceof Long l) {
                    lastLongArg = l;
                }
            }
            if (lastLongArg != null) {
                return lastLongArg.toString();
            }
        }
        return null;
    }

    private String buildSuccessDetails(AuditAction action, String entityType, String entityId, Object result) {
        if (result instanceof MangaResponse r) {
            return String.format("%s executed on %s (id=%d, title='%s')", action, entityType, r.id(), r.title());
        }
        if (result instanceof OrderResponse r) {
            return String.format("%s executed on %s (id=%d, status=%s, finalPrice=%s)", action, entityType, r.id(), r.status(), r.finalPrice());
        }
        if (result instanceof List<?> list) {
            return String.format("%s executed on %s (entityId=%s, itemsCount=%d)", action, entityType, entityId, list.size());
        }
        if (entityId != null) {
            return String.format("%s executed successfully on %s (id=%s)", action, entityType, entityId);
        }
        return String.format("%s executed successfully on %s", action, entityType);
    }

    private String resolveIpAddress() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
            HttpServletRequest request = attrs.getRequest();
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null && !forwarded.isBlank()) {
                return forwarded.split(",")[0].trim();
            }
            return request.getRemoteAddr();
        }
        return "INTERNAL";
    }

    private boolean isUuidToken(String value) {
        return value != null && value.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    }

    private record ActorInfo(Long actorId, String actorUsername) {
    }
}
