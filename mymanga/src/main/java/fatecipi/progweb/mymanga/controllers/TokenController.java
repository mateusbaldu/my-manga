package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.dto.security.LoginRequestDto;
import fatecipi.progweb.mymanga.dto.security.LoginResponseDto;
import fatecipi.progweb.mymanga.models.user.Users;
import fatecipi.progweb.mymanga.services.TokenService;
import fatecipi.progweb.mymanga.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/my-manga")
public class TokenController {
    @Autowired
    public TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(tokenService.login(loginRequestDto));
    }
}
