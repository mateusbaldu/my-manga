package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.models.dto.security.LoginRequest;
import fatecipi.progweb.mymanga.models.dto.security.LoginResponse;
import fatecipi.progweb.mymanga.models.dto.user.UserCreate;
import fatecipi.progweb.mymanga.models.dto.user.UserResponse;
import fatecipi.progweb.mymanga.models.dto.user.UserUpdate;
import fatecipi.progweb.mymanga.services.TokenService;
import fatecipi.progweb.mymanga.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/my-manga/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    public TokenService tokenService;

    @PostMapping("/new")
    public ResponseEntity<UserResponse> create(@RequestBody UserCreate userCreate) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(userCreate));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(tokenService.login(loginRequest));
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
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@RequestBody Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{username}")
    public ResponseEntity<UserResponse> update(@RequestBody UserUpdate userUpdate, @PathVariable String username) {
        return ResponseEntity.ok(userService.update(userUpdate, username));
    }
}
