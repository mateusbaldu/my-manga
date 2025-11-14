package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.exceptions.PermissionDeniedException;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.dto.address.AddressCreate;
import fatecipi.progweb.mymanga.dto.address.AddressResponse;
import fatecipi.progweb.mymanga.dto.address.AddressUpdate;
import fatecipi.progweb.mymanga.services.AddressService;
import fatecipi.progweb.mymanga.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/my-manga/users")
@RequiredArgsConstructor
public class AddressController {
    private final UserService userService;
    private final AddressService addressService;

    @PostMapping("/{username}/address/new")
    public ResponseEntity<AddressResponse> addNewAddressToUser(@PathVariable("username") String username, @Valid @RequestBody AddressCreate dto, JwtAuthenticationToken token
    ) {
        verifyUserPermission(username, token);
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
    public ResponseEntity<Page<AddressResponse>> getAllAddressesFromUser(@PathVariable("username") String username, Pageable pageable, JwtAuthenticationToken token
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
            throw new PermissionDeniedException("User don't have permission to access the address by other account");
        }
    }
}
