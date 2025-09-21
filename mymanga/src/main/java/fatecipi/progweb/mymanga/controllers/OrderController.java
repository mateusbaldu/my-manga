package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.exceptions.InvalidLoginException;
import fatecipi.progweb.mymanga.exceptions.NotPermittedException;
import fatecipi.progweb.mymanga.models.Order;
import fatecipi.progweb.mymanga.models.Role;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.models.dto.order.OrderCreate;
import fatecipi.progweb.mymanga.models.dto.order.OrderResponse;
import fatecipi.progweb.mymanga.services.OrderService;
import fatecipi.progweb.mymanga.services.UserService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/my-manga/orders")
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    private UserService userService;

    @PostMapping("/new")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderCreate orderDto, JwtAuthenticationToken token) {
        Users user = userService.findByIdWithoutDto(Long.valueOf(token.getName()));
        OrderResponse order = orderService.create(orderDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<OrderResponse>> listAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<OrderResponse>> findByUserUsername(@PathVariable String username) {
        return ResponseEntity.ok(orderService.findByUserUsername(username));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id, JwtAuthenticationToken token) {
        isUserPermitted(id, token);
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> update(@PathVariable Long id, OrderCreate orderDto, JwtAuthenticationToken token) {
        isUserPermitted(id, token);
        return ResponseEntity.ok(orderService.update(id, orderDto));
    }

    private void isUserPermitted(@PathVariable Long id, JwtAuthenticationToken token) {
        Order order = orderService.findByIdWithoutDto(id);
        Users user = userService.findByIdWithoutDto(Long.valueOf(token.getName()));
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));
        if(!order.getUsers().getId().equals(user.getId()) || !isAdmin) {
            throw new NotPermittedException("Esse pedido não está associado ao usuário " + user.getName());
        }
    }
}
