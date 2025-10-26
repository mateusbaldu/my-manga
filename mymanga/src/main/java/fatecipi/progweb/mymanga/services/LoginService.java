package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.configs.security.TokenConfig;
import fatecipi.progweb.mymanga.exceptions.InvalidLoginException;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.Role;
import fatecipi.progweb.mymanga.models.dto.security.LoginRequest;
import fatecipi.progweb.mymanga.models.dto.security.LoginResponse;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TokenConfig tokenConfig;

    public LoginResponse login(LoginRequest loginRequest) {
        Users user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new ResourceNotFoundException("User with email "+ loginRequest.email() +" not found"));
        if (!user.isLoginCorrect(loginRequest, passwordEncoder)) {
            throw new InvalidLoginException("Invalid password");
        }
        if (!user.isActive()) {
            throw new InvalidLoginException("User account is not active");
        }
        long expiresIn = 1800L;

        String jwtValue = tokenConfig.generateToken(user);
        return new LoginResponse(jwtValue, expiresIn);
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
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired token."));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setConfirmationToken(null);
        userRepository.save(user);
    }
}
