package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.mappers.UserMapper;
import fatecipi.progweb.mymanga.exceptions.ResourceAlreadyExistsException;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.dto.user.UserCreate;
import fatecipi.progweb.mymanga.dto.user.UserResponse;
import fatecipi.progweb.mymanga.dto.user.UserUpdate;
import fatecipi.progweb.mymanga.repositories.RoleRepository;
import fatecipi.progweb.mymanga.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.UUID;

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

    private Users user;
    private UserResponse userResponse;
    private UserUpdate userUpdate;
    private UserCreate userCreate;

    @BeforeEach
    void setUp() {
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
        userUpdate = new UserUpdate(
                "Test",
                "email@email.com",
                "test123"
        );
        userResponse = new UserResponse(
                "Test",
                "test123",
                Instant.now(),
                null
        );
        userCreate = new UserCreate(
                "Test",
                "email@email.com",
                "test123",
                "password"
        );
    }

    @Nested
    class findAll {
        @Test
        @DisplayName("should return a Page of UserResponse when everything is ok")
        void findAll_returnPageResponse_whenEverythingIsOk() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Users> userPage = new PageImpl<>(List.of(user));

            doReturn(userPage).when(userRepository).findAll(any(Pageable.class));
            doReturn(userResponse).when(userMapper).responseMapping(any(Users.class));

            var output = userService.findAll(pageable);

            assertNotNull(output);
            assertEquals(userPage.getTotalElements(), output.getTotalElements());
            verify(userMapper, times(1)).responseMapping(user);
            verify(userRepository, times(1)).findAll(pageable);
        }
    }

    @Nested
    class getUserByUsername {
        @Test
        @DisplayName("should return a User when everything is ok")
        void getUserByUsername_returnUser_whenEverythingIsOk() {
            String username = "test123";

            doReturn(Optional.of(user)).when(userRepository).findByUsername(anyString());

            var output = userService.getUserByUsername(username);

            assertNotNull(output);
            assertEquals(user, output);
            verify(userRepository, times(1)).findByUsername(username);
        }

        @Test
        @DisplayName("should throw a ResourceNotFoundException when the User isn't found")
        void getUserByUsername_throwResourceNotFoundException_whenUserIsNotFound() {
            doReturn(Optional.empty()).when(userRepository).findByUsername(anyString());

            assertThrows(ResourceNotFoundException.class, () -> userService.getUserByUsername(anyString()));
        }
    }

    @Nested
    class getUserResponseByUsername {
        @Test
        @DisplayName("should return a UserResponse when everything is ok")
        void getUserResponseByUsername_returnUserResponse_whenEverythingIsOk() {
            String username = "test123";

            doReturn(Optional.of(user)).when(userRepository).findByUsername(anyString());
            doReturn(userResponse).when(userMapper).responseMapping(any(Users.class));

            var output = userService.getUserResponseByUsername(username);

            assertNotNull(output);
            assertEquals(user.getName(), output.name());
            verify(userRepository, times(1)).findByUsername(username);
            verify(userMapper, times(1)).responseMapping(user);
        }

        @Test
        @DisplayName("should throw a ResourceNotFoundException when the User isn't found")
        void getUserResponseByUsername_throwResourceNotFoundException_whenUserIsNotFound() {
            doReturn(Optional.empty()).when(userRepository).findByUsername(anyString());

            assertThrows(ResourceNotFoundException.class, () -> userService.getUserResponseByUsername(anyString()));
        }
    }

    @Nested
    class getUserById {
        @Test
        @DisplayName("should return a User when everything is ok")
        void getUserById_returnUser_whenEverythingIsOk() {
            long id = user.getId();

            doReturn(Optional.of(user)).when(userRepository).findById(anyLong());

            var output = userService.getUserById(id);

            assertNotNull(output);
            assertEquals(user, output);
            verify(userRepository, times(1)).findById(id);
        }

        @Test
        @DisplayName("should throw a ResourceNotFoundException when the User isn't found")
        void getUserById_throwResourceNotFoundException_whenUserIsNotFound() {
            doReturn(Optional.empty()).when(userRepository).findById(anyLong());

            assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(anyLong()));
        }
    }

    @Nested
    class getUserResponseById{
        @Test
        @DisplayName("should return a UserResponse when everything is ok")
        void getUserResponseById_returnUserResponse_whenEverythingIsOk() {
            long id = 1L;

            doReturn(Optional.of(user)).when(userRepository).findById(anyLong());
            doReturn(userResponse).when(userMapper).responseMapping(any(Users.class));

            var output = userService.getUserResponseById(id);

            assertNotNull(output);
            assertEquals(user.getName(), output.name());
            verify(userRepository, times(1)).findById(id);
            verify(userMapper, times(1)).responseMapping(user);
        }

        @Test
        @DisplayName("should throw a ResourceNotFoundException when the User isn't found")
        void getUserResponseById_throwResourceNotFoundException_whenUserIsNotFound() {
            doReturn(Optional.empty()).when(userRepository).findById(anyLong());

            assertThrows(ResourceNotFoundException.class, () -> userService.getUserResponseById(anyLong()));
        }
    }

    @Nested
    class deleteById {
        @Test
        @DisplayName("should return void when User deleted succesfully")
        void deleteById_returnVoid_whenEverythingIsOk() {
            long id = 1L;
            doReturn(Optional.of(user)).when(userRepository).findById(anyLong());
            doNothing().when(userRepository).delete(any());

            userService.deleteById(id);

            verify(userRepository, times(1)).findById(id);
            verify(userRepository, times(1)).delete(user);
        }

        @Test
        @DisplayName("should throw a ResourceNotFoundException when the User isn't found")
        void deleteById_throwResourceNotFoundException_whenUserIsNotFound() {
            doReturn(Optional.empty()).when(userRepository).findById(anyLong());

            assertThrows(ResourceNotFoundException.class, () -> userService.deleteById(anyLong()));
        }
    }

    @Nested
    class update {
        @Test
        @DisplayName("should return a UserResponse successfully when the user is updated")
        void update_returnUserResponse_whenUserIsUpdated() {
            String username = "test123";

            doReturn(Optional.of(user)).when(userRepository).findByUsername(anyString());
            doNothing().when(userMapper).updateMapping(any(UserUpdate.class), any(Users.class));
            doReturn(user).when(userRepository).save(any());
            doReturn(userResponse).when(userMapper).responseMapping(any(Users.class));

            var output = userService.update(userUpdate, username);

            assertNotNull(output);
            assertEquals(user.getName(), output.name());
            verify(userRepository, times(1)).findByUsername(anyString());
            verify(userMapper, times(1)).updateMapping(userUpdate, user);
            verify(userRepository, times(1)).save(user);
            verify(userMapper, times(1)).responseMapping(user);
        }

        @Test
        @DisplayName("should throw a ResourceNotFoundException when the User isn't found")
        void findByUsername_throwResourceNotFoundException_whenUserIsNotFound() {
            String username = "test123";

            doReturn(Optional.empty()).when(userRepository).findByUsername(anyString());

            assertThrows(ResourceNotFoundException.class, () -> userService.update(userUpdate, username));
        }
    }

    @Nested
    class create {
        @Test
        @DisplayName("should return a UserResponse when the User is created successfully")
        void create_returnUserResponse_whenUserIsCreated() {
            //TODO
        }


        @Test
        @DisplayName("should throw a ResourceAlreadyExists when the User already exists")
        void create_throwResourceAlreadyExistsException_whenUserIsAlreadyExists() {
            doReturn(Optional.of(user)).when(userRepository).findByEmail(anyString());

            assertThrows(ResourceAlreadyExistsException.class, () -> userService.create(userCreate));
        }
    }

    @Nested
    class activateAccount {
        @Test
        @DisplayName("should return void when everything is ok")
        void activateAccount_returnVoid_WhenEverythingIsOk() {
            String token = UUID.randomUUID().toString();
            doReturn(Optional.of(user)).when(userRepository).findByConfirmationToken(anyString());

            userService.activateAccount(token);

            assertTrue(user.isActive());
            assertNull(user.getConfirmationToken());
            verify(userRepository, times(1)).findByConfirmationToken(token);
        }

        @Test
        @DisplayName("Should throw a ResourceNotFoundException when the User isn't found by the token")
        void activateAccount_throwResourceNotFoundException_WhenUserIsNotFound() {
            String token = UUID.randomUUID().toString();
            doReturn(Optional.empty()).when(userRepository).findByConfirmationToken(anyString());

            assertThrows(ResourceNotFoundException.class, () -> userService.activateAccount(token));
            verify(userRepository, times(1)).findByConfirmationToken(token);
        }
    }
}