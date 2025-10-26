package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.configs.security.TokenConfig;
import fatecipi.progweb.mymanga.exceptions.InvalidLoginException;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.Role;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.models.dto.security.LoginRequest;
import fatecipi.progweb.mymanga.models.dto.security.ResetPasswordRequest;
import fatecipi.progweb.mymanga.repositories.RoleRepository;
import fatecipi.progweb.mymanga.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
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
    @Mock
    TokenConfig tokenConfig;

    @InjectMocks
    LoginService loginService;

    @Captor
    ArgumentCaptor<LoginRequest> loginRequestCaptor;
    @Captor
    ArgumentCaptor<JwtEncoderParameters> jwtEncoderParametersCaptor;

    private Users user;
    private Set<Role> roles = new HashSet<>();
    private LoginRequest loginRequest;
    private String token;
    private ResetPasswordRequest resetPasswordRequest;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setId(Role.Values.BASIC.getRoleId());
        role.setName(Role.Values.BASIC.name());
        roles.add(role);
        user = Users.builder()
                .id(1L)
                .email("email@email.com")
                .username("test123")
                .name("Test")
                .password("password")
                .createdAt(Instant.now())
                .isActive(true)
                .confirmationToken(null)
                .address(null)
                .roles(roles)
                .orders(null)
                .build();
        loginRequest = new LoginRequest(
                "email@email.com",
                "password");
        token = UUID.randomUUID().toString();
        resetPasswordRequest = new ResetPasswordRequest(
                token,
                "newPassword"
        );
    }

    @Nested
    class login {
        @Test
        @DisplayName("should return a LoginResponse when everything is ok")
        void login_returnLoginResponse_WhenEverythingIsOk() {
            String scopes = roles.stream().map(Role::getName).collect(Collectors.joining(" "));
            long expiresIn = 200000L;
            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .subject(user.getName())
                    .expiresAt(Instant.now().plusSeconds(expiresIn))
                    .claim("scope", scopes)
                    .build();
            Jwt jwt = Jwt.withTokenValue("token-test")
                    .header("Test", "none")
                    .claim("scope", scopes)
                    .build();
            String fakeTokenValue = jwt.getTokenValue();

            doReturn(Optional.of(user)).when(userRepository).findByEmail(anyString());
            when(user.isLoginCorrect(loginRequest, passwordEncoder)).thenReturn(true);
            doReturn(fakeTokenValue).when(tokenConfig).generateToken(any(Users.class));

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
            doReturn(Optional.empty()).when(userRepository).findByEmail(anyString());

            assertThrows(ResourceNotFoundException.class, () -> loginService.login(loginRequest));
            verify(userRepository, times(1)).findByEmail(loginRequest.email());
        }

        @Test
        @DisplayName("should throw a InvalidLoginException when the password is invalid")
        void login_throwInvalidLoginException_WhenPasswordIsInvalid() {
            doReturn(Optional.of(user)).when(userRepository).findByEmail(anyString());
            when(user.isLoginCorrect(loginRequest, passwordEncoder)).thenReturn(false);

            assertThrows(InvalidLoginException.class, () -> loginService.login(loginRequest));
            verify(userRepository, times(1)).findByEmail(loginRequest.email());
        }

        @Test
        @DisplayName("should throw a InvalidLoginException when the User isn't active")
        void login_throwInvalidLoginException_WhenUserIsntActive() {
            user.setActive(false);
            doReturn(Optional.of(user)).when(userRepository).findByEmail(anyString());
            when(user.isLoginCorrect(loginRequest, passwordEncoder)).thenReturn(true);

            assertThrows(InvalidLoginException.class, () -> loginService.login(loginRequest));
            verify(userRepository, times(1)).findByEmail(loginRequest.email());
        }
    }

    @Nested
    class requestPasswordReset {
        @Test
        @DisplayName("should return void when everything is working successfully")
        void requestPasswordReset_returnVoid_WhenEverythingIsOk() {
            String email = user.getEmail();

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
            String email = user.getEmail();
            doReturn(Optional.empty()).when(userRepository).findByEmail(email);

            assertThrows(ResourceNotFoundException.class, () -> loginService.requestPasswordReset(email));
        }
    }

    @Nested
    class resetPassword {
        @Test
        @DisplayName("should return void when everything is working successfully")
        void resetPassword_returnVoid_WhenEverythingIsOk() {
            doReturn(Optional.of(user)).when(userRepository).findByConfirmationToken(anyString());
            doReturn(resetPasswordRequest.newPassword()).when(passwordEncoder).encode(anyString());
            doReturn(user).when(userRepository).save(any(Users.class));

            loginService.resetPassword(resetPasswordRequest);

            verify(userRepository, times(1)).findByConfirmationToken(resetPasswordRequest.token());
            verify(userRepository, times(1)).save(user);
            verify(passwordEncoder, times(1)).encode(resetPasswordRequest.newPassword());
        }

        @Test
        @DisplayName("should throw a ResourceNotFoundException when the User isn't found")
        void resetPassword_throwResourceNotFoundException_WhenUserIsNotFound() {
            doReturn(Optional.empty()).when(userRepository).findByConfirmationToken(token);

            assertThrows(ResourceNotFoundException.class, () -> loginService.resetPassword(resetPasswordRequest));
            verify(userRepository, times(1)).findByConfirmationToken(token);
        }
    }
}