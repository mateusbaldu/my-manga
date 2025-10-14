package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.configs.mappers.UserMapper;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.models.dto.user.UserResponse;
import fatecipi.progweb.mymanga.repositories.RoleRepository;
import fatecipi.progweb.mymanga.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @Nested
    class findAll {
        @Test
        @DisplayName("should return a Page of UserResponse when everything is ok")
        void findAll_returnPageResponse_whenEverythingIsOk() {
            Pageable pageable = PageRequest.of(0, 10);
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
            UserResponse userResponse = new UserResponse(
                    "test123",
                    "Test",
                    Instant.now(),
                    null,
                    null
            );
            Page<Users> userPage = new PageImpl<>(List.of(user));

            doReturn(userPage).when(userRepository).findAll(any(Pageable.class));
            doReturn(userResponse).when(userMapper).toUserResponse(any(Users.class));

            var output = userService.findAll(pageable);

            assertNotNull(output);
            assertEquals(userPage.getTotalElements(), output.getTotalElements());
            verify(userMapper, times(1)).toUserResponse(user);
            verify(userRepository, times(1)).findAll(pageable);
        }
    }

    @Nested
    class findByUsernameWithoutDto {
        @Test
        @DisplayName("should return a User when everything is ok")
        void findByUsernameWithoutDto_returnUser_whenEverythingIsOk() {
            String username = "test123";
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

            doReturn(Optional.of(user)).when(userRepository).findByUsername(anyString());

            var output = userService.findByUsernameWithoutDto(username);

            assertNotNull(output);
            assertEquals(user, output);
            verify(userRepository, times(1)).findByUsername(username);
        }

        @Test
        @DisplayName("should throw a ResourceNotFoundException when the User isn't found")
        void findByUsernameWithoutDto_throwResourceNotFoundException_whenUserIsNotFound() {
            doReturn(Optional.empty()).when(userRepository).findByUsername(anyString());

            assertThrows(ResourceNotFoundException.class, () -> userService.findByUsernameWithoutDto(anyString()));
        }
    }

    @Nested
    class findByUsername {
        @Test
        @DisplayName("should return a UserResponse when everything is ok")
        void findByUsername_returnUserResponse_whenEverythingIsOk() {
            String username = "test123";
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
            UserResponse userResponse = new UserResponse(
                    "Test",
                    "test123",
                    Instant.now(),
                    null,
                    null
            );

            doReturn(Optional.of(user)).when(userRepository).findByUsername(anyString());
            doReturn(userResponse).when(userMapper).toUserResponse(any(Users.class));

            var output = userService.findByUsername(username);

            assertNotNull(output);
            assertEquals(user.getName(), output.name());
            verify(userRepository, times(1)).findByUsername(username);
            verify(userMapper, times(1)).toUserResponse(user);
        }

        @Test
        @DisplayName("should throw a ResourceNotFoundException when the User isn't found")
        void findByUsername_throwResourceNotFoundException_whenUserIsNotFound() {
            doReturn(Optional.empty()).when(userRepository).findByUsername(anyString());

            assertThrows(ResourceNotFoundException.class, () -> userService.findByUsername(anyString()));
        }
    }

    @Nested
    class findByIdWithoutDto {
        @Test
        @DisplayName("should return a User when everything is ok")
        void findByIdWithoutDto_returnUser_whenEverythingIsOk() {
            long id = 1L;
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

            doReturn(Optional.of(user)).when(userRepository).findById(anyLong());

            var output = userService.findByIdWithoutDto(id);

            assertNotNull(output);
            assertEquals(user, output);
            verify(userRepository, times(1)).findById(id);
        }

        @Test
        @DisplayName("should throw a ResourceNotFoundException when the User isn't found")
        void findByIdWithoutDto_throwResourceNotFoundException_whenUserIsNotFound() {
            doReturn(Optional.empty()).when(userRepository).findById(anyLong());

            assertThrows(ResourceNotFoundException.class, () -> userService.findByIdWithoutDto(anyLong()));
        }
    }

    @Nested
    class findById {
        @Test
        @DisplayName("should return a UserResponse when everything is ok")
        void findById_returnUserResponse_whenEverythingIsOk() {
            long id = 1L;
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
            UserResponse userResponse = new UserResponse(
                    "Test",
                    "test123",
                    Instant.now(),
                    null,
                    null
            );

            doReturn(Optional.of(user)).when(userRepository).findById(anyLong());
            doReturn(userResponse).when(userMapper).toUserResponse(any(Users.class));

            var output = userService.findById(id);

            assertNotNull(output);
            assertEquals(user.getName(), output.name());
            verify(userRepository, times(1)).findById(id);
            verify(userMapper, times(1)).toUserResponse(user);
        }

        @Test
        @DisplayName("should throw a ResourceNotFoundException when the User isn't found")
        void findById_throwResourceNotFoundException_whenUserIsNotFound() {
            doReturn(Optional.empty()).when(userRepository).findById(anyLong());

            assertThrows(ResourceNotFoundException.class, () -> userService.findById(anyLong()));
        }
    }

    //TODO: pesquisar como usar generics para mesclar as classes de pesquisa por Id e por username

    @Nested
    class deleteById {
        //TODO: implementar
    }

    @Nested
    class update {
        //TODO: implementar
    }

    @Nested
    class create {
        //TODO: implementar
    }
}