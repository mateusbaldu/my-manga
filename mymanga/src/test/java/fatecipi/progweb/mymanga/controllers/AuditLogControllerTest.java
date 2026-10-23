package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.dto.audit.AuditLogResponse;
import fatecipi.progweb.mymanga.dto.audit.AuditLogSearchFilter;
import fatecipi.progweb.mymanga.models.enums.AuditAction;
import fatecipi.progweb.mymanga.models.enums.AuditStatus;
import fatecipi.progweb.mymanga.services.AuditLogService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest(
        controllers = AuditLogController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class}
)
class AuditLogControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuditLogService auditLogService;

    private AuditLogResponse auditLogResponse;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);

        auditLogResponse = new AuditLogResponse(
                1L,
                AuditAction.MANGA_CREATED,
                "Manga",
                "1",
                1L,
                "admin",
                "127.0.0.1",
                AuditStatus.SUCCESS,
                "MANGA_CREATED executed on Manga (id=1, title='Naruto')",
                Instant.now()
        );
    }

    @Nested
    class listAll {
        @Test
        @DisplayName("GET /my-manga/audit-logs - should return Page of AuditLogResponse when everything is ok")
        void listAll_returnPageAuditLogResponse_WhenEverythingIsOk() {
            Page<AuditLogResponse> page = new PageImpl<>(List.of(auditLogResponse), PageRequest.of(0, 10), 1);
            when(auditLogService.listAll(any(Pageable.class), any(AuditLogSearchFilter.class))).thenReturn(page);

            RestAssuredMockMvc
                    .given()
                    .when()
                    .get("/audit-logs?page=0&size=10")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("totalElements", equalTo(1))
                    .body("content[0].id", equalTo(1))
                    .body("content[0].action", equalTo("MANGA_CREATED"))
                    .body("content[0].actorUsername", equalTo("admin"));

            verify(auditLogService, times(1)).listAll(any(Pageable.class), any(AuditLogSearchFilter.class));
        }
    }

    @Nested
    class getById {
        @Test
        @DisplayName("GET /my-manga/audit-logs/{id} - should return AuditLogResponse when everything is ok")
        void getById_returnAuditLogResponse_WhenEverythingIsOk() {
            when(auditLogService.getById(1L)).thenReturn(auditLogResponse);

            RestAssuredMockMvc
                    .given()
                    .when()
                    .get("/audit-logs/{id}", 1L)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(1))
                    .body("action", equalTo("MANGA_CREATED"))
                    .body("entityType", equalTo("Manga"));

            verify(auditLogService, times(1)).getById(1L);
        }
    }
}
