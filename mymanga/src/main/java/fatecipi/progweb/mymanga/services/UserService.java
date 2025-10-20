package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.mappers.UserMapper;
import fatecipi.progweb.mymanga.exceptions.ResourceAlreadyExistsException;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.Role;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.models.dto.user.UserCreate;
import fatecipi.progweb.mymanga.models.dto.user.UserResponse;
import fatecipi.progweb.mymanga.models.dto.user.UserUpdate;
import fatecipi.progweb.mymanga.repositories.RoleRepository;
import fatecipi.progweb.mymanga.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toUserResponse);
    }

    public Users getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User with username "+ username +" not found"));
    }

    public UserResponse getUserResponseByUsername(String username) {
        Users user = getUserByUsername(username);
        return userMapper.toUserResponse(user);
    }

    public Users getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with id "+ id +" not found"));
    }

    public UserResponse getUserResponseById(Long id) {
        Users user = getUserById(id);
        return userMapper.toUserResponse(user);
    }

    public void deleteById(Long id) {
        userRepository.delete(getUserById(id));
    }

    public UserResponse update(UserUpdate dto, String username) {
        Users user = getUserByUsername(username);
        userMapper.mapUpdateUser(dto, user);
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }
    public UserResponse create(UserCreate dto) {
        if(userRepository.findByEmail(dto.email()).isPresent()) {
            throw new ResourceAlreadyExistsException("User with email"+ dto.email() +" already exists");
        }
        Role role = roleRepository.findByName(Role.Values.BASIC.name());
        Users newUser = new Users();
        userMapper.mapCreateUser(dto, newUser);
        String token = UUID.randomUUID().toString();
        newUser.setPassword(passwordEncoder.encode(dto.password()));
        newUser.setRoles(Set.of(role));
        newUser.setCreatedAt(Instant.now());
        newUser.setActive(false);
        newUser.setConfirmationToken(token);
        userRepository.save(newUser);

        String activationUrl = "http://localhost:8080/my-manga/users/activate?token=" + token;
        String subject = "Activate your account on My Mangá!";
        String body = "Welcome, " + newUser.getName() + "! Click on the link below to activate your account:\n\n" + activationUrl;
        emailService.sendEmail(newUser.getEmail(), subject, body);

        return userMapper.toUserResponse(newUser);
    }
}
