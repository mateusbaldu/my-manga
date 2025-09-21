package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.models.dto.user.UserCreate;
import fatecipi.progweb.mymanga.models.dto.user.UserResponse;
import fatecipi.progweb.mymanga.models.dto.user.UserUpdate;
import fatecipi.progweb.mymanga.exceptions.ResourceAlreadyExistsException;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.Role;
import fatecipi.progweb.mymanga.configs.mappers.UserMapper;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.repositories.RoleRepository;
import fatecipi.progweb.mymanga.repositories.UserRepository;
import org.mapstruct.control.MappingControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> userMapper.toUserResponse(user))
                .collect(Collectors.toList());
    }

    public Users findByUsernameWithoutDto(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User with username "+ username +" not found"));
    }

    public UserResponse findByUsername(String username) {
        Users user = findByUsernameWithoutDto(username);
        return userMapper.toUserResponse(user);
    }

    public Users findByIdWithoutDto(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with id "+ id +" not found"));
    }

    public UserResponse findById(Long id) {
        Users user = findByIdWithoutDto(id);
        return userMapper.toUserResponse(user);
    }

    public void deleteById(Long id) {
        userRepository.delete(findByIdWithoutDto(id));
    }

    public UserResponse update(UserUpdate dto, String username) {
        Users user = findByUsernameWithoutDto(username);
        userMapper.mapUpdateUser(dto, user);
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    public UserResponse create(UserCreate dto) {
        Role role = roleRepository.findByName(Role.Values.BASIC.name());
        if(userRepository.findByEmail(dto.email()).isPresent()) {
            throw new ResourceAlreadyExistsException("User with email"+ dto.email() +" already exists");
        }
        Users newUser = new Users();
        userMapper.mapCreateUser(dto, newUser);
        newUser.setPassword(passwordEncoder.encode(dto.password()));
        newUser.setRoles(Set.of(role));
        newUser.setCreatedAt(Instant.now());
        userRepository.save(newUser);
        return userMapper.toUserResponse(newUser);
    }
}
