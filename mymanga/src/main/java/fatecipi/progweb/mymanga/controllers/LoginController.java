package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.dto.security.ForgotPasswordRequest;
import fatecipi.progweb.mymanga.dto.security.LoginRequest;
import fatecipi.progweb.mymanga.dto.security.LoginResponse;
import fatecipi.progweb.mymanga.dto.security.ResetPasswordRequest;
import fatecipi.progweb.mymanga.services.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Login", description = "Endpoints for user login and password reset")
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @Operation(summary = "Authenticate a user and return a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful, JWT token returned"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(loginService.login(loginRequest));
    }

    @Operation(summary = "Request a password reset email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset request processed successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        loginService.requestPasswordReset(request.email());
        return ResponseEntity.ok("If the user exists, a reset link has been sent to the email.");
    }

    @Operation(summary = "Reset user password using a valid reset token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired token"),
            @ApiResponse(responseCode = "404", description = "Token not found")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        loginService.resetPassword(request);
        return ResponseEntity.ok("Successful password reset! You now can log in with the new password.");
    }
}
