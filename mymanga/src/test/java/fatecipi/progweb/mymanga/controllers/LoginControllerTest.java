package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.dto.security.ForgotPasswordRequest;
import fatecipi.progweb.mymanga.dto.security.LoginRequest;
import fatecipi.progweb.mymanga.dto.security.LoginResponse;
import fatecipi.progweb.mymanga.dto.security.ResetPasswordRequest;
import fatecipi.progweb.mymanga.services.LoginService;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@WebMvcTest(LoginController.class)
class LoginControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockitoBean
    private LoginService loginService;

    private Users user;
    private LoginRequest loginRequest;
    private LoginResponse loginResponse;
    private ForgotPasswordRequest forgotPasswordRequest;
    private ResetPasswordRequest resetPasswordRequest;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mvc);

        user = new Users(
                1L,
                "email@email.com",
                "test123",
                "Test",
                "password",
                Instant.now(),
                true,
                null,
                null,
                null,
                null
        );
        loginRequest = new LoginRequest(
                "email@email.com",
                "password"
        );
        String tokenValue = "";
        loginResponse = new LoginResponse(
                tokenValue,
                1800L
        );
        forgotPasswordRequest = new ForgotPasswordRequest(
                "email@email.com"
        );
        String token = UUID.randomUUID().toString();
        resetPasswordRequest = new ResetPasswordRequest(
                token,
                "newPassword"
        );
    }

    @Nested
    class login {
        @Test
        @DisplayName("POST /my-manga/login - should return Login Response when everything is ok")
        void login_returnLoginResponse_whenEverythingIsOk() {
            doReturn(loginResponse).when(loginService).login(any(LoginRequest.class));

            RestAssuredMockMvc
                    .given()
                    .contentType(ContentType.JSON)
                    .body(loginRequest)
                    .postProcessors(
                            jwt().jwt(j -> j.subject(user.getId().toString())),
                            csrf()
                    )
                    .when()
                    .post("/my-manga/login")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("expiresIn", equalTo(1800));
            verify(loginService, times(1)).login(loginRequest);
        }
    }

    @Nested
    class forgotPassword {
        @Test
        @DisplayName("POST /my-manga/login/forgot-password - should return a message when everything is ok")
        void forgotPassword_returnMessage_whenEverythingIsOk() {
            doNothing().when(loginService).requestPasswordReset(anyString());

            RestAssuredMockMvc
                    .given()
                    .contentType(ContentType.JSON)
                    .body(forgotPasswordRequest)
                    .postProcessors(
                            jwt().jwt(j -> j.subject(user.getId().toString())),
                            csrf()
                    )
                    .when()
                    .post("/my-manga/login/forgot-password")
                    .then()
                    .statusCode(HttpStatus.OK.value());
            verify(loginService, times(1)).requestPasswordReset(forgotPasswordRequest.email());
        }
    }

    @Nested
    class resetPassword {
        @Test
        @DisplayName("POST /my-manga/login/reset-password - should return a message when everything is ok")
        void resetPassword_returnMessage_whenEverythingIsOk() {
            doNothing().when(loginService).resetPassword(any(ResetPasswordRequest.class));

            RestAssuredMockMvc
                    .given()
                    .contentType(ContentType.JSON)
                    .body(resetPasswordRequest)
                    .postProcessors(
                            jwt().jwt(j -> j.subject(user.getId().toString())),
                            csrf()
                    )
                    .when()
                    .post("/my-manga/login/reset-password")
                    .then()
                    .statusCode(HttpStatus.OK.value());
            verify(loginService, times(1)).resetPassword(resetPasswordRequest);
        }
    }
}