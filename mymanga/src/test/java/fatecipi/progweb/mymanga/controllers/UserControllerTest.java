package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.models.dto.security.LoginRequest;
import fatecipi.progweb.mymanga.models.dto.security.LoginResponse;
import fatecipi.progweb.mymanga.models.dto.user.UserCreate;
import fatecipi.progweb.mymanga.models.dto.user.UserResponse;
import fatecipi.progweb.mymanga.models.dto.user.UserUpdate;
import fatecipi.progweb.mymanga.services.LoginService;
import fatecipi.progweb.mymanga.services.UserService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hibernate.annotations.DialectOverride;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private LoginService loginService;
    @MockitoBean
    private UserService userService;

    private Users user;
    private UserCreate userCreate;
    private UserResponse userResponse;
    private UserUpdate userUpdate;

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
        userCreate = new UserCreate(
                "Test",
                "email@email.com",
                "test123",
                "password"
        );
        userResponse = new UserResponse(
                "Test",
                "test123",
                Instant.now(),
                null
        );
        userUpdate = new UserUpdate(
                "Test updated",
                "updatedemail@email.com",
                "test123updated"
        );
    }

    @Nested
    class create {
        @Test
        @DisplayName("POST /my-manga/users/new - should return User Response when everything is ok")
        void create_returnUserResponse_whenEverythingIsOk() {
            doReturn(userResponse).when(userService).create(any(UserCreate.class));

            RestAssuredMockMvc
                    .given()
                    .contentType(ContentType.JSON)
                    .body(userCreate)
                    .postProcessors(
                            jwt().jwt(j -> j.subject(user.getId().toString())),
                            csrf()
                    )
                    .when()
                    .post("/my-manga/users/new")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("username", equalTo("test123"));
            verify(userService, times(1)).create(userCreate);
        }
    }

    @Nested
    class getUserByUsername {
        @Test
        @DisplayName("GET /my-manga/users/{username} - should return User Response when everything is ok")
        void getUserByUsername_returnUserResponse_whenEverythingIsOk() {
            doReturn(userResponse).when(userService).getUserResponseByUsername(anyString());

            RestAssuredMockMvc
                    .given()
                    .postProcessors(
                            jwt().jwt(j -> j.subject(user.getId().toString())),
                            csrf()
                    )
                    .when()
                    .get("/my-manga/users/{username}", "test123")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("name", equalTo("Test"));
            verify(userService, times(1)).getUserResponseByUsername("test123");
        }
    }

    @Nested
    class deleteById {
        @Test
        @DisplayName("DELETE /my-manga/users/{id} - should return No Content when everything is ok")
        void deleteById_returnNoContent_whenEverythingIsOk() {
            doReturn(user).when(userService).getUserById(anyLong());
            doNothing().when(userService).deleteById(anyLong());

            RestAssuredMockMvc
                    .given()
                    .postProcessors(
                            jwt().jwt(j -> j.subject(user.getId().toString())),
                            csrf()
                    )
                    .when()
                    .delete("/my-manga/users/{id}", 1L)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
            verify(userService, times(1)).getUserById(1L);
            verify(userService, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("DELETE /my-manga/users/{id} - should throw Exception when the User Id don't match the Token Id")
        void create_throwBadCredentialsException_whenUserIdDontMatchTokenId() {
            doReturn(user).when(userService).getUserById(anyLong());

            RestAssuredMockMvc
                    .given()
                    .postProcessors(
                            jwt().jwt(j -> j.subject(String.valueOf(2L))),
                            csrf()
                    )
                    .when()
                    .delete("/my-manga/users/{id}", 1L)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
            verify(userService, times(1)).getUserById(1L);
        }
    }

    @Nested
    class update {
        @Test
        @DisplayName("PATCH /my-manga/users/{username} - should return User Response when everything is ok")
        void update_returnUserResponse_whenEverythingIsOk() {
            userResponse = new UserResponse(
                    "Test updated",
                    "test123updated",
                    Instant.now(),
                    null
            );

            doReturn(user).when(userService).getUserByUsername(anyString());
            doReturn(userResponse).when(userService).update(any(UserUpdate.class), anyString());

            RestAssuredMockMvc
                    .given()
                    .contentType(ContentType.JSON)
                    .body(userUpdate)
                    .postProcessors(
                            jwt().jwt(j -> j.subject(user.getId().toString())),
                            csrf()
                    )
                    .when()
                    .patch("/my-manga/users/{username}", "test123")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("username", equalTo("test123updated"));
            verify(userService, times(1)).getUserByUsername("test123");
            verify(userService, times(1)).update(userUpdate, "test123");
        }

        @Test
        @DisplayName("PATCH /my-manga/users/{username} - should throw Exception when the User Id don't match the Token Id")
        void update_throwBadCredentialsException_whenUserIdDontMatchTokenId() {
            doReturn(user).when(userService).getUserByUsername(anyString());

            RestAssuredMockMvc
                    .given()
                    .contentType(ContentType.JSON)
                    .body(userUpdate)
                    .postProcessors(
                            jwt().jwt(j -> j.subject(String.valueOf(2L))),
                            csrf()
                    )
                    .when()
                    .patch("/my-manga/users/{username}", "test123")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
            verify(userService, times(1)).getUserByUsername("test123");
        }
    }
}