package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.exceptions.InvalidLoginException;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.Role;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.models.dto.security.LoginRequest;
import fatecipi.progweb.mymanga.repositories.RoleRepository;
import fatecipi.progweb.mymanga.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    BCryptPasswordEncoder passwordEncoder;
    @Mock
    EmailService emailService;
    @Mock
    RoleRepository roleRepository;
    @Mock
    JwtEncoder jwtEncoder;

    @InjectMocks
    LoginService loginService;

    @Captor
    ArgumentCaptor<LoginRequest> loginRequestCaptor;
    @Captor
    ArgumentCaptor<JwtEncoderParameters> jwtEncoderParametersCaptor;


    @Nested
    class login {
        @Test
        @DisplayName("should return a LoginResponse when everything is ok")
        void login_returnLoginResponse_WhenEverythingIsOk() {
            Instant now = Instant.now();

            LoginRequest loginRequest = new LoginRequest(
                    "email@email.com",
                    "password");
            Role role = new Role();
            role.setId(Role.Values.BASIC.getRoleId());
            role.setName(Role.Values.BASIC.name());
            Users user = new Users(
                    1L,
                    "email@email.com",
                    "test123",
                    "Test",
                    "password",
                    now,
                    true,
                    null,
                    null,
                    Set.of(role),
                    null
            );

            String scopes = role.getName();
            long expiresIn = 200000L;
            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .subject(user.getName())
                    .expiresAt(now.plusSeconds(expiresIn))
                    .claim("scope", scopes)
                    .build();
            Jwt jwt = Jwt.withTokenValue("token-test")
                    .header("Test", "none")
                    .claim("scope", scopes)
                    .build();

            doReturn(Optional.of(user)).when(userRepository).findByEmail(anyString());
            when(user.isLoginCorrect(loginRequest, passwordEncoder)).thenReturn(true);
            doReturn(jwt).when(jwtEncoder).encode(any(JwtEncoderParameters.class));

            var output = loginService.login(loginRequest);

            assertNotNull(output);
            assertEquals(claims.getSubject(), user.getName());
            assertEquals(claims.getClaim("scope"), scopes);
            assertTrue(user.isLoginCorrect(loginRequest, passwordEncoder));
            verify(userRepository, times(1)).findByEmail(loginRequest.email());
        }

        @Test
        @DisplayName("should throw a ResourceNotFoundException when the User isn't found by email")
        void login_throwResourceNotFoundException_WhenUserNotFound() {
            LoginRequest loginRequest = new LoginRequest(
                    "email@email.com",
                    "password");
            doReturn(Optional.empty()).when(userRepository).findByEmail(anyString());

            assertThrows(ResourceNotFoundException.class, () -> loginService.login(loginRequest));
            verify(userRepository, times(1)).findByEmail(loginRequest.email());
        }

        @Test
        @DisplayName("should throw a InvalidLoginException when the password is invalid")
        void login_throwInvalidLoginException_WhenPasswordIsInvalid() {
            LoginRequest loginRequest = new LoginRequest(
                    "email@email.com",
                    "password");
            Users user = new Users(
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
            doReturn(Optional.of(user)).when(userRepository).findByEmail(anyString());
            when(user.isLoginCorrect(loginRequest, passwordEncoder)).thenReturn(false);

            assertThrows(InvalidLoginException.class, () -> loginService.login(loginRequest));
            verify(userRepository, times(1)).findByEmail(loginRequest.email());
        }

        @Test
        @DisplayName("should throw a InvalidLoginException when the User isn't active")
        void login_throwInvalidLoginException_WhenUserIsntActive() {
            LoginRequest loginRequest = new LoginRequest(
                    "email@email.com",
                    "password");
            Users user = new Users(
                    1L,
                    "email@email.com",
                    "test123",
                    "Test",
                    "password",
                    Instant.now(),
                    false,
                    null,
                    null,
                    null,
                    null
            );
            doReturn(Optional.of(user)).when(userRepository).findByEmail(anyString());
            when(user.isLoginCorrect(loginRequest, passwordEncoder)).thenReturn(true);

            assertThrows(InvalidLoginException.class, () -> loginService.login(loginRequest));
            verify(userRepository, times(1)).findByEmail(loginRequest.email());
        }
    }

    @Nested
    class activateAccount {
        @Test
        @DisplayName("should return void when everything is ok")
        void activateAccount_returnVoid_WhenEverythingIsOk() {
            String token = UUID.randomUUID().toString();
            Users user = new Users(
                    1L,
                    "email@email.com",
                    "test123",
                    "Test",
                    "password",
                    Instant.now(),
                    false,
                    token,
                    null,
                    null,
                    null
            );
            doReturn(Optional.of(user)).when(userRepository).findByConfirmationToken(anyString());

            loginService.activateAccount(token);

            assertTrue(user.isActive());
            assertNull(user.getConfirmationToken());
            verify(userRepository, times(1)).findByConfirmationToken(token);
        }

        @Test
        @DisplayName("Should throw a ResourceNotFoundException when the User isn't found by the token")
        void activateAccount_throwResourceNotFoundException_WhenUserIsNotFound() {
            String token = UUID.randomUUID().toString();
            doReturn(Optional.empty()).when(userRepository).findByConfirmationToken(anyString());

            assertThrows(ResourceNotFoundException.class, () -> loginService.activateAccount(token));
            verify(userRepository, times(1)).findByConfirmationToken(token);
        }
    }

    @Nested
    class requestPasswordReset {
        @Test
        @DisplayName("should return void when everything is working successfully")
        void requestPasswordReset_returnVoid_WhenEverythingIsOk() {
            String email = "email@email.com";
            Users user = new Users(
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

            doReturn(Optional.of(user)).when(userRepository).findByEmail(anyString());
            doReturn(user).when(userRepository).save(any(Users.class));
            doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

            loginService.requestPasswordReset(email);

            assertNotNull(user.getConfirmationToken());
            verify(userRepository, times(1)).findByEmail(email);
            verify(userRepository, times(1)).save(user);
            verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("should throw a ResourceNotFoundException when the User isn't found")
        void requestPasswordReset_throwResourceNotFoundException_WhenUserIsNotFound() {
            String email = "email@email.com";
            doReturn(Optional.empty()).when(userRepository).findByEmail(email);

            assertThrows(ResourceNotFoundException.class, () -> loginService.requestPasswordReset(email));
        }
    }

    @Nested
    class resetPassword {
        @Test
        @DisplayName("should return void when everything is working successfully")
        void resetPassword_returnVoid_WhenEverythingIsOk() {
            String password = "password";
            String encodedPassword = "encodedPassword";
            String token = UUID.randomUUID().toString();
            Users user = new Users(
                    1L,
                    "email@email.com",
                    "test123",
                    "Test",
                    "password",
                    Instant.now(),
                    true,
                    token,
                    null,
                    null,
                    null
            );

            doReturn(Optional.of(user)).when(userRepository).findByConfirmationToken(token);
            doReturn(encodedPassword).when(passwordEncoder).encode(password);

            loginService.resetPassword(token, password);

            assertNull(user.getConfirmationToken());
            assertEquals(encodedPassword, user.getPassword());
            verify(userRepository, times(1)).findByConfirmationToken(token);
            verify(passwordEncoder, times(1)).encode(password);
        }

        @Test
        @DisplayName("should throw a ResourceNotFoundException when the User isn't found")
        void resetPassword_throwResourceNotFoundException_WhenUserIsNotFound() {
            String password = "password";
            String token = UUID.randomUUID().toString();

            doReturn(Optional.empty()).when(userRepository).findByConfirmationToken(token);

            assertThrows(ResourceNotFoundException.class, () -> loginService.resetPassword(token, password));
            verify(userRepository, times(1)).findByConfirmationToken(token);
        }
    }
}