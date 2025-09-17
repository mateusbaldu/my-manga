package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.dto.security.LoginRequestDto;
import fatecipi.progweb.mymanga.dto.security.LoginResponseDto;
import fatecipi.progweb.mymanga.models.user.Users;
import fatecipi.progweb.mymanga.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class TokenService {
    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public TokenService(JwtEncoder jwtEncoder, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Optional<Users> user = userRepository.findByEmail(loginRequestDto.email());
        if (user.isEmpty() || !user.get().isLoginCorrect(loginRequestDto, passwordEncoder)) {
            throw new BadCredentialsException("Invalid email or password");
        }
        Instant now = Instant.now();
        long expiresIn = 1800L;
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("my-mangá.api")
                .subject(user.get().getId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .build();
        String jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return new LoginResponseDto(jwtValue, expiresIn);
    }
}
