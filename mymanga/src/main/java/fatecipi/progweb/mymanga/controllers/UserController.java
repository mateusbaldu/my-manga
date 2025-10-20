package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.models.dto.address.AddressCreate;
import fatecipi.progweb.mymanga.models.dto.address.AddressResponse;
import fatecipi.progweb.mymanga.models.dto.address.AddressUpdate;
import fatecipi.progweb.mymanga.models.dto.security.ForgotPasswordRequest;
import fatecipi.progweb.mymanga.models.dto.security.LoginRequest;
import fatecipi.progweb.mymanga.models.dto.security.LoginResponse;
import fatecipi.progweb.mymanga.models.dto.security.ResetPasswordRequest;
import fatecipi.progweb.mymanga.models.dto.user.UserCreate;
import fatecipi.progweb.mymanga.models.dto.user.UserResponse;
import fatecipi.progweb.mymanga.models.dto.user.UserUpdate;
import fatecipi.progweb.mymanga.services.AddressService;
import fatecipi.progweb.mymanga.services.LoginService;
import fatecipi.progweb.mymanga.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/my-manga/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final LoginService loginService;
    private final AddressService addressService;

    @PostMapping("/new")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreate userCreate) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(userCreate));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(loginService.login(loginRequest));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> findByUsername(@PathVariable String username, JwtAuthenticationToken token) {
        return ResponseEntity.ok(userService.getUserResponseByUsername(username));
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserResponseById(id));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Page<UserResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, JwtAuthenticationToken token) {
        Users user = userService.getUserById(id);
        if (!user.getId().equals(Long.valueOf(token.getName()))) {
            throw new BadCredentialsException("User don't have permission to delete another account");
        }
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{username}")
    public ResponseEntity<UserResponse> update(@Valid @RequestBody UserUpdate userUpdate, @PathVariable String username, JwtAuthenticationToken token) {
        Users user = userService.getUserByUsername(username);
        if (!user.getId().equals(Long.valueOf(token.getName()))) {
            throw new BadCredentialsException("User don't have permission to delete another account");
        }
        return ResponseEntity.ok(userService.update(userUpdate, username));
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam("token") String token) {
        loginService.activateAccount(token);
        return ResponseEntity.ok("Account activated successfully! You now can log in!");
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



    @PostMapping("/{username}/address/new")
    public ResponseEntity<AddressResponse> addNewAddressToUser(@PathVariable("username") String username, @Valid @RequestBody AddressCreate dto, JwtAuthenticationToken token
    ) {
        Users user = userService.getUserByUsername(username);
        if (!user.getId().equals(Long.valueOf(token.getName()))) {
            throw new BadCredentialsException("User don't have permission to add new address to other account");
        }
        return ResponseEntity.ok(addressService.addNewAddressToUser(username, dto));
    }

    @GetMapping("/{username}/address/{addressid}")
    public ResponseEntity<AddressResponse> getAddressById(@PathVariable("username") String username, @PathVariable("addressid") Long addressid, JwtAuthenticationToken token
    ) {
        verifyUserPermission(username, token);
        return ResponseEntity.ok(addressService.getAddressResponseById(username, addressid));
    }

    @DeleteMapping("/{username}/address/{addressid}")
    public ResponseEntity<Void> deleteAddressById(@PathVariable("username") String username, @PathVariable("addressid") Long addressid, JwtAuthenticationToken token
    ) {
        verifyUserPermission(username, token);
        addressService.deleteAddressById(username, addressid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{username}/address/all")
    public ResponseEntity<Page<AddressResponse>> getAllAddresses(@PathVariable("username") String username, Pageable pageable, JwtAuthenticationToken token
    ) {
        verifyUserPermission(username, token);
        return ResponseEntity.ok(addressService.getUserAddresses(username, pageable));
    }

    @PatchMapping("/{username}/address/{addressid}")
    public ResponseEntity<AddressResponse> updateAddress(@PathVariable("username") String username, @PathVariable("addressid") Long addressid, @Valid @RequestBody AddressUpdate update, JwtAuthenticationToken token) {
        verifyUserPermission(username, token);
        return ResponseEntity.ok(addressService.updateAddressById(username, addressid, update));
    }

    private void verifyUserPermission(String username, JwtAuthenticationToken token) {
        Users user = userService.getUserByUsername(username);
        if (!user.getId().equals(Long.valueOf(token.getName()))) {
            throw new BadCredentialsException("User don't have permission to access the address by other account");
        }
    }
}
