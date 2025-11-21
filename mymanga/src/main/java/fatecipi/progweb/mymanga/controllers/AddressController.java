package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.exceptions.PermissionDeniedException;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.dto.address.AddressCreate;
import fatecipi.progweb.mymanga.dto.address.AddressResponse;
import fatecipi.progweb.mymanga.dto.address.AddressUpdate;
import fatecipi.progweb.mymanga.services.AddressService;
import fatecipi.progweb.mymanga.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Addresses", description = "Endpoints for user address management")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class AddressController {
    private final UserService userService;
    private final AddressService addressService;

    @Operation(summary = "Create a new address for a user from a user username and an address creation body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "User don't have permission to access address from other account")
    })
    @PostMapping("/{username}/address/new")
    public ResponseEntity<AddressResponse> addNewAddressToUser(@PathVariable("username") String username, @Valid @RequestBody AddressCreate dto, JwtAuthenticationToken token
    ) {
        verifyUserPermission(username, token);
        return ResponseEntity.ok(addressService.addNewAddressToUser(username, dto));
    }

    @Operation(summary = "Search an address by user username and address id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address found successfully"),
            @ApiResponse(responseCode = "404", description = "User not found / Address not found"),
            @ApiResponse(responseCode = "403", description = "User don't have permission to access address from other account")
    })
    @GetMapping("/{username}/address/{addressid}")
    public ResponseEntity<AddressResponse> getAddressById(@PathVariable("username") String username, @PathVariable("addressid") Long addressid, JwtAuthenticationToken token
    ) {
        verifyUserPermission(username, token);
        return ResponseEntity.ok(addressService.getAddressResponseById(username, addressid));
    }

    @Operation(summary = "Delete an address by user username and address id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Address deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found / Address not found"),
            @ApiResponse(responseCode = "403", description = "User don't have permission to access address from other account")
    })
    @DeleteMapping("/{username}/address/{addressid}")
    public ResponseEntity<Void> deleteAddressById(@PathVariable("username") String username, @PathVariable("addressid") Long addressid, JwtAuthenticationToken token
    ) {
        verifyUserPermission(username, token);
        addressService.deleteAddressById(username, addressid);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List all addresses of a user by user username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Addresses found successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "User don't have permission to access address from other account")
    })
    @GetMapping("/{username}/address/all")
    public ResponseEntity<Page<AddressResponse>> getAllAddressesFromUser(@PathVariable("username") String username, Pageable pageable, JwtAuthenticationToken token
    ) {
        verifyUserPermission(username, token);
        return ResponseEntity.ok(addressService.getUserAddresses(username, pageable));
    }

    @Operation(summary = "Update an address from a user username, address id and an address update body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User not found / Address not found"),
            @ApiResponse(responseCode = "403", description = "User don't have permission to access address from other account")
    })
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
