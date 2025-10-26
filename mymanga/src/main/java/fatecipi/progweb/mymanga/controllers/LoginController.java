package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.models.dto.security.ForgotPasswordRequest;
import fatecipi.progweb.mymanga.models.dto.security.LoginRequest;
import fatecipi.progweb.mymanga.models.dto.security.LoginResponse;
import fatecipi.progweb.mymanga.models.dto.security.ResetPasswordRequest;
import fatecipi.progweb.mymanga.services.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/my-manga/login")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(loginService.login(loginRequest));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        loginService.requestPasswordReset(request.email());
        return ResponseEntity.ok("If the user exists, a reset link has been sent to the email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        loginService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok("Successful password reset! You now can log in with the new password.");
    }

}
