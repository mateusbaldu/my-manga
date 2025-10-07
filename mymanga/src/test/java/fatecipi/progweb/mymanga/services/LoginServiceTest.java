package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.models.Role;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.models.dto.security.LoginRequest;
import fatecipi.progweb.mymanga.models.dto.security.LoginResponse;
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
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    class Login {
        @Test
        @DisplayName("should return a loginresponse when a login is made successfully")
        void login_returnLoginResponse_whenEverythingIsOk() {
            //Arrange
            Instant now = Instant.now();
            Role role = new Role();
            role.setName(Role.Values.BASIC.name());
            LoginRequest loginRequest = new LoginRequest(
                    "test@email.com",
                    "password");
            Users user = new Users(
                    1L,
                    "test@email.com",
                    "test123",
                    "Teste",
                    "password",
                    now,
                    true,
                    null,
                    null,
                    Set.of(role),
                    null
            );
            Jwt fakeJwt = Jwt.withTokenValue("fake-token")
                    .header("alg", "none")
                    .claim("scope", "BASIC")
                    .subject(user.getUsername())
                    .build();

            doReturn(Optional.of(user)).when(userRepository).findByEmail("test@email.com");
            when(passwordEncoder.matches(loginRequest.password(), user.getPassword())).thenReturn(true);
            when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(fakeJwt);

            //Act
            LoginResponse response = loginService.login(loginRequest);

            //Assert
            assertNotNull(response);
            assertEquals(fakeJwt.getTokenValue(), response.accessToken());

            verify(userRepository, times(1)).findByEmail(loginRequest.email());
            verify(passwordEncoder, times(1)).matches(loginRequest.password(), user.getPassword());

            verify(jwtEncoder, times(1)).encode(jwtEncoderParametersCaptor.capture());

            var capturedClaims = jwtEncoderParametersCaptor.getValue().getClaims();
            assertEquals(user.getUsername(), capturedClaims.getSubject());
        }
    }
}