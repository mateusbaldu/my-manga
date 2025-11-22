package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.configs.security.TokenConfig;
import fatecipi.progweb.mymanga.exceptions.InvalidLoginException;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.dto.security.LoginRequest;
import fatecipi.progweb.mymanga.dto.security.LoginResponse;
import fatecipi.progweb.mymanga.listeners.PasswordResetRequestedEvent;
import fatecipi.progweb.mymanga.listeners.UserEventListener;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.dto.security.ResetPasswordRequest;
import fatecipi.progweb.mymanga.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TokenConfig tokenConfig;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
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

    @Transactional
    public void requestPasswordReset(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));

        if (!user.isActive()) {
            throw new InvalidLoginException("User account is not active");
        }

        String token = UUID.randomUUID().toString();
        user.setConfirmationToken(token);
        userRepository.save(user);

        eventPublisher.publishEvent(new PasswordResetRequestedEvent(user));
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        Users user = userRepository.findByConfirmationToken(request.token())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired token."));

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setConfirmationToken(null);
        userRepository.save(user);
    }
}
