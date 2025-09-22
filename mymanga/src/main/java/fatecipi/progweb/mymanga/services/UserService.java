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
import java.util.UUID;
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
    @Autowired
    private EmailService emailService;

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

    public void activateAccount(String token) {
        Users user = userRepository.findByConfirmationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token de ativação inválido."));

        user.setActive(true);
        user.setConfirmationToken(null);
        userRepository.save(user);
    }

    public void requestPasswordReset(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));

        String token = UUID.randomUUID().toString();
        user.setConfirmationToken(token);
        userRepository.save(user);

        String subject = "Instruções para Redefinição de Senha - My Mangá";
        String body = String.format("""
        Olá %s,
        
        Você solicitou a redefinição da sua senha. Como não temos um frontend, por favor, siga as instruções abaixo para criar uma nova senha usando uma ferramenta de API (como Postman, Insomnia, etc.):
        
        1. Método HTTP:
           POST
        2. URL do Endpoint:
           http://localhost:8080/my-manga/users/reset-password
           
        3. No corpo (Body) da requisição, envie o seguinte JSON:
           (Lembre-se de configurar o Header 'Content-Type' para 'application/json')
           {
               "token": "%s",
               "newPassword": "SUA_NOVA_SENHA_AQUI"
           }
        INSTRUÇÕES IMPORTANTES:
        - Copie o token exatamente como está.
        - Substitua "SUA_NOVA_SENHA_AQUI" pela nova senha que você deseja.
        
        Se você não solicitou isso, pode ignorar este e-mail.
        """, user.getName(), token);

        emailService.sendEmail(user.getEmail(), subject, body);
    }

    public void resetPassword(String token, String newPassword) {
        Users user = userRepository.findByConfirmationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("TInvalid or expired token."));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setConfirmationToken(null);
        userRepository.save(user);
    }
}
