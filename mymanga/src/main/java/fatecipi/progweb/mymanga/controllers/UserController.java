package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.exceptions.NotPermittedException;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.models.dto.security.ForgotPasswordRequest;
import fatecipi.progweb.mymanga.models.dto.security.LoginRequest;
import fatecipi.progweb.mymanga.models.dto.security.LoginResponse;
import fatecipi.progweb.mymanga.models.dto.security.ResetPasswordRequest;
import fatecipi.progweb.mymanga.models.dto.user.UserCreate;
import fatecipi.progweb.mymanga.models.dto.user.UserResponse;
import fatecipi.progweb.mymanga.models.dto.user.UserUpdate;
import fatecipi.progweb.mymanga.services.LoginService;
import fatecipi.progweb.mymanga.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/my-manga/users")
public class UserController {
    private final UserService userService;
    private final LoginService loginService;

    public UserController(UserService userService, LoginService loginService) {
        this.userService = userService;
        this.loginService = loginService;
    }

    @PostMapping("/new")
    public ResponseEntity<UserResponse> create(@RequestBody UserCreate userCreate) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(userCreate));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(loginService.login(loginRequest));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> findByUsername(@PathVariable String username, JwtAuthenticationToken token) {
        return ResponseEntity.ok(userService.findByUsername(username));
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Page<UserResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, JwtAuthenticationToken token) {
        Users user = userService.findByIdWithoutDto(id);
        if (!user.getId().equals(Long.valueOf(token.getName()))) {
            throw new NotPermittedException("User don't have permission to delete another account");
        }
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{username}")
    public ResponseEntity<UserResponse> update(@RequestBody UserUpdate userUpdate, @PathVariable String username, JwtAuthenticationToken token) {
        Users user = userService.findByUsernameWithoutDto(username);
        if (!user.getId().equals(Long.valueOf(token.getName()))) {
            throw new NotPermittedException("User don't have permission to delete another account");
        }
        return ResponseEntity.ok(userService.update(userUpdate, username));
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam("token") String token) {
        loginService.activateAccount(token);
        return ResponseEntity.ok("Account activated successfully! You now can log in!");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        loginService.requestPasswordReset(request.email());
        return ResponseEntity.ok("If the user exists, a reset link has been sent to the email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        loginService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok("Successful password reset! You now can log in with the new password.");
    }
}
