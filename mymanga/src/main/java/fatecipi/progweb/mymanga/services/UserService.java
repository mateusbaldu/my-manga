package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.listeners.UserRegisteredEvent;
import fatecipi.progweb.mymanga.mappers.UserMapper;
import fatecipi.progweb.mymanga.exceptions.ResourceAlreadyExistsException;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.Role;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.dto.user.UserCreate;
import fatecipi.progweb.mymanga.dto.user.UserResponse;
import fatecipi.progweb.mymanga.dto.user.UserUpdate;
import fatecipi.progweb.mymanga.repositories.RoleRepository;
import fatecipi.progweb.mymanga.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::responseMapping);
    }

    public Users getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User with username "+ username +" not found"));
    }

    @Transactional(readOnly = true)
    public UserResponse getUserResponseByUsername(String username) {
        Users user = getUserByUsername(username);
        return userMapper.responseMapping(user);
    }

    public Users getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with id "+ id +" not found"));
    }

    @Transactional(readOnly = true)
    public UserResponse getUserResponseById(Long id) {
        Users user = getUserById(id);
        return userMapper.responseMapping(user);
    }

    @Transactional
    public void deleteById(Long id) {
        userRepository.delete(getUserById(id));
    }

    @Transactional
    public UserResponse update(UserUpdate dto, String username) {
        Users user = getUserByUsername(username);
        userMapper.updateMapping(dto, user);
        userRepository.save(user);
        return userMapper.responseMapping(user);
    }

    @Transactional
    public UserResponse create(UserCreate dto) {
        if(userRepository.findByEmail(dto.email()).isPresent()) {
            throw new ResourceAlreadyExistsException("User with email"+ dto.email() +" already exists");
        }
        Role role = roleRepository.findByName(Role.Values.BASIC.name());
        Users newUser = new Users();
        userMapper.createMapping(dto, newUser);
        String token = UUID.randomUUID().toString();
        newUser.setPassword(passwordEncoder.encode(dto.password()));
        newUser.setRoles(Set.of(role));
        newUser.setCreatedAt(Instant.now());
        newUser.setActive(false);
        newUser.setConfirmationToken(token);
        userRepository.save(newUser);

        eventPublisher.publishEvent(new UserRegisteredEvent(newUser));

        return userMapper.responseMapping(newUser);
    }

    @Transactional
    public void activateAccount(String token) {
        Users user = userRepository.findByConfirmationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid authentication token"));

        user.setActive(true);
        user.setConfirmationToken(null);
        userRepository.save(user);
    }
}
