package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.exceptions.NotAvailableException;
import fatecipi.progweb.mymanga.exceptions.PermissionDeniedException;
import fatecipi.progweb.mymanga.models.Order;
import fatecipi.progweb.mymanga.models.Role;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.models.dto.order.OrderCreate;
import fatecipi.progweb.mymanga.models.dto.order.OrderResponse;
import fatecipi.progweb.mymanga.services.OrderService;
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
@RequestMapping("/my-manga/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    @PostMapping("/new")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderCreate orderDto, JwtAuthenticationToken token) {
        Users user = userService.getUserById(Long.valueOf(token.getName()));
        OrderResponse order = orderService.create(orderDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderResponseById(id));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<Page<OrderResponse>> findByUserUsername(@PathVariable String username, Pageable pageable) {
        return ResponseEntity.ok(orderService.findAllByUserUsername(username, pageable));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<Page<OrderResponse>> findByUserId(Pageable pageable, JwtAuthenticationToken token) {
        long userId = Long.parseLong(token.getName());
        Users user = userService.getUserById(userId);
        if (!user.isActive()) {
            throw new PermissionDeniedException("This account is inactive.");
        }
        return ResponseEntity.ok(orderService.findAllByUserId(userId, pageable));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrderById(@PathVariable Long id, JwtAuthenticationToken token) {
        isUserPermitted(id, token);
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> update(@PathVariable Long id, @Valid @RequestBody OrderCreate orderDto, JwtAuthenticationToken token) {
        isUserPermitted(id, token);
        return ResponseEntity.ok(orderService.update(id, orderDto));
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmOrder(@RequestParam("token") String token) {
        orderService.confirmOrder(token);
        return ResponseEntity.ok("Order confirmed successfully!");
    }

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
