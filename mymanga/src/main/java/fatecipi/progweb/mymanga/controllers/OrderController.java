
package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.exceptions.PermissionDeniedException;
import fatecipi.progweb.mymanga.models.Order;
import fatecipi.progweb.mymanga.models.Role;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.dto.order.OrderCreate;
import fatecipi.progweb.mymanga.dto.order.OrderResponse;
import fatecipi.progweb.mymanga.services.OrderService;
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

@Tag(name = "Orders", description = "Endpoints for order management and confirmation")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    @Operation(summary = "Create a new order from a order creation body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User not found / volume with id not found"),
            @ApiResponse(responseCode = "403", description = "User account isn't active")
    })
    @PostMapping("/new")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderCreate orderDto, JwtAuthenticationToken token) {
        Users user = userService.getUserById(Long.valueOf(token.getName()));
        OrderResponse order = orderService.create(orderDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @Operation(summary = "Search a order by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found successfully"),
            @ApiResponse(responseCode = "404", description = "Order with id not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderResponseById(id));
    }

    @Operation(summary = "Search all orders related to a user by the username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders found successfully"),
            @ApiResponse(responseCode = "404", description = "User with username not found")
    })
    @GetMapping("/user/{username}")
    public ResponseEntity<Page<OrderResponse>> findByUserUsername(@PathVariable String username, Pageable pageable) {
        return ResponseEntity.ok(orderService.findAllByUserUsername(username, pageable));
    }

    @Operation(summary = "Search all orders related to a user by the id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders found successfully"),
            @ApiResponse(responseCode = "404", description = "User with id not found"),
            @ApiResponse(responseCode = "403", description = "User account isn't active")
    })
    @GetMapping("/my-orders")
    public ResponseEntity<Page<OrderResponse>> findByUserId(Pageable pageable, JwtAuthenticationToken token) {
        long userId = Long.parseLong(token.getName());
        Users user = userService.getUserById(userId);
        if (!user.isActive()) {
            throw new PermissionDeniedException("This account is inactive.");
        }
        return ResponseEntity.ok(orderService.findAllByUserId(userId, pageable));
    }

    @Operation(summary = "Cancel the order by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Order with id not found"),
            @ApiResponse(responseCode = "400", description = "Order can't be cancelled")
    })
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrderById(@PathVariable Long id, JwtAuthenticationToken token) {
        isUserPermitted(id, token);
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Updates the order from a order update body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "User account isn't active / Order isn't associated with user"),
            @ApiResponse(responseCode = "404", description = "Volume with id not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> update(@PathVariable Long id, @Valid @RequestBody OrderCreate orderDto, JwtAuthenticationToken token) {
        isUserPermitted(id, token);
        return ResponseEntity.ok(orderService.update(id, orderDto));
    }

    @Operation(summary = "Confirms the order from a token",
    description = "confirm a order from a token (random uuid), which is required as a parameter of the request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order confirmed successfully!"),
            @ApiResponse(responseCode = "404", description = "Invalid or expired confirmation token"),
            @ApiResponse(responseCode = "400", description = "Order alredy confirmed")
    })
    @GetMapping("/confirm")
    public ResponseEntity<String> confirmOrder(@RequestParam("token") String token) {
        orderService.confirmOrder(token);
        return ResponseEntity.ok("Order confirmed successfully!");
    }

    @Operation(summary = "List all available orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders found successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized")
    })
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Page<OrderResponse>> listAll(Pageable pageable) {
        return ResponseEntity.ok(orderService.findAll(pageable));
    }

    private void isUserPermitted(Long id, JwtAuthenticationToken token) {
        Order order = orderService.getOrderById(id);
        Users user = userService.getUserById(Long.valueOf(token.getName()));
        if (!user.isActive()) {
            throw new PermissionDeniedException("This account is inactive.");
        }
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));
        if(!order.getUsers().getId().equals(user.getId()) && !isAdmin) {
            throw new PermissionDeniedException("This order is not associated with user " + user.getName());
        }
    }
}
