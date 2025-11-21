package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.exceptions.PermissionDeniedException;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.dto.user.UserCreate;
import fatecipi.progweb.mymanga.dto.user.UserResponse;
import fatecipi.progweb.mymanga.dto.user.UserUpdate;
import fatecipi.progweb.mymanga.services.LoginService;
import fatecipi.progweb.mymanga.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users", description = "Endpoints for user management and account activation")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final LoginService loginService;

    @Operation(summary = "Create a new user from a user creation body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "User with email already exists")
    })
    @PostMapping("/new")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreate userCreate) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(userCreate));
    }

    @Operation(summary = "Search a user by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found successully"),
            @ApiResponse(responseCode = "404", description = "User with username not found")
    })
    @GetMapping
    public ResponseEntity<UserResponse> getUserByUsername(@RequestParam String username, JwtAuthenticationToken token) {
        return ResponseEntity.ok(userService.getUserResponseByUsername(username));
    }

    @Operation(summary = "Delete a user by user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted sucessfully"),
            @ApiResponse(responseCode = "404", description = "User with id not found"),
            @ApiResponse(responseCode = "403", description = "Unauthorized")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id, JwtAuthenticationToken token) {
        Users user = userService.getUserById(id);
        if (!user.getId().equals(Long.valueOf(token.getName()))) {
            throw new PermissionDeniedException("User don't have permission to delete another account");
        }
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update a user from a username and a user update body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User with username not found"),
            @ApiResponse(responseCode = "403", description = "Unauthorized")
    })
    @PatchMapping("/{username}")
    public ResponseEntity<UserResponse> update(@Valid @RequestBody UserUpdate userUpdate, @PathVariable String username, JwtAuthenticationToken token) {
        Users user = userService.getUserByUsername(username);
        if (!user.getId().equals(Long.valueOf(token.getName()))) {
            throw new PermissionDeniedException("User don't have permission to delete another account");
        }
        return ResponseEntity.ok(userService.update(userUpdate, username));
    }

    @Operation(summary = "activate a user account by a token",
            description = "activate a non-active user account based on a token (random uuid), which is required as a parameter of the request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account activated sucessfully"),
            @ApiResponse(responseCode = "404", description = "Invalid activation token")
    })
    @GetMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam("token") String token) {
        userService.activateAccount(token);
        return ResponseEntity.ok("Account activated successfully! You now can log in!");
    }

    @Operation(summary = "Search a user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found successfully"),
            @ApiResponse(responseCode = "404", description = "User with id not found"),
            @ApiResponse(responseCode = "403", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserResponseById(id));
    }

    @Operation(summary = "List all users available")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized")
    })
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Page<UserResponse>> listAll(Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(pageable));
    }
}
