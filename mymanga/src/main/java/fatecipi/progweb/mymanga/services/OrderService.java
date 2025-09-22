package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.configs.mappers.OrderMapper;
import fatecipi.progweb.mymanga.enums.OrderStatus;
import fatecipi.progweb.mymanga.exceptions.NotAvailableException;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.*;
import fatecipi.progweb.mymanga.models.dto.order.OrderCreate;
import fatecipi.progweb.mymanga.models.dto.order.OrderResponse;
import fatecipi.progweb.mymanga.repositories.OrderRepository;
import fatecipi.progweb.mymanga.repositories.UserRepository;
import fatecipi.progweb.mymanga.repositories.VolumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private MangaService mangaService;
    @Autowired
    private VolumeRepository volumeRepository;
    @Autowired
    private EmailService emailService;

    public Page<OrderResponse> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable).map(order -> orderMapper.toOrderResponse(order));
    }

    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " not found"));
        return orderMapper.toOrderResponse(order);
    }

    public Order findByIdWithoutDto(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " not found"));
    }

    public Page<OrderResponse> findByUserUsername(String username, Pageable pageable) {
        if (!userRepository.existsByUsername(username)) {
            throw new ResourceNotFoundException("User with username " + username + " not found");
        }
        Page<Order> orderPage = orderRepository.findByUsers_Username(username, pageable);
        return orderPage.map(order -> orderMapper.toOrderResponse(order));
    }

    public void delete(Long id) {
        orderRepository.delete(findByIdWithoutDto(id));
    }

    public OrderResponse update(Long id, OrderCreate orderDto) {
        Order order = findByIdWithoutDto(id);
        orderMapper.mapOrder(orderDto, order);
        orderRepository.save(order);
        return orderMapper.toOrderResponse(order);
    }

    public OrderResponse create(OrderCreate orderDto, Users user) {
        List<OrderItems> orderItemsList = orderDto.items().stream()
                .map(itemDto -> {
                    Volume volume = mangaService.findByIdNoDto(itemDto.volumeId());
                    if (volume.getQuantity() < itemDto.quantity()) {
                        throw new NotAvailableException(volume.getManga().getTitle() + " Vol. " + volume.getVolumeNumber() + " is not available.");
                    }
                    volume.setQuantity(volume.getQuantity() - itemDto.quantity());
                    volumeRepository.save(volume);
                    return OrderItems.builder()
                            .volumeId(volume.getId())
                            .title(volume.getManga().getTitle()  + " Vol. " + volume.getVolumeNumber())
                            .quantity(itemDto.quantity())
                            .unitPrice(volume.getPrice())
                            .totalPrice(volume.getPrice().multiply(BigDecimal.valueOf(itemDto.quantity())))
                            .build();
                })
                .collect(Collectors.toList());

        BigDecimal totalPrice = orderItemsList.stream()
                .map(OrderItems::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean isSubscriber = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(Role.Values.SUBSCRIBER.name()));
        if (isSubscriber) {
            totalPrice = totalPrice.multiply(new BigDecimal("0.80")).setScale(2, RoundingMode.HALF_UP);
        }

        String token = UUID.randomUUID().toString();
        Order newOrder = Order.builder()
                .users(user)
                .paymentMethod(orderDto.paymentMethod())
                .createdAt(Instant.now())
                .finalPrice(totalPrice)
                .items(orderItemsList)
                .confirmationToken(token)
                .status(OrderStatus.WAITING_CONFIRMATION)
                .build();
        orderItemsList.forEach(item -> item.setOrder(newOrder));
        orderRepository.save(newOrder);

        String confirmationUrl = "http://localhost:8080/my-manga/orders/confirm?token=" + token;
        String subject = "Confirm your order #" + newOrder.getId();
        String body = "Hi " + user.getName() + ",\n\nThank you for buying with us! Please, confirm your order by clicking on the link down below:\n\n" + confirmationUrl;
        emailService.sendEmail(user.getEmail(), subject, body);

        return orderMapper.toOrderResponse(newOrder);
    }

    public void confirmOrder(String token) {
        Order order = orderRepository.findByConfirmationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired confirmation token"));

        if (order.getStatus() == OrderStatus.WAITING_CONFIRMATION) {
            order.setStatus(OrderStatus.CONFIRMED);
            order.setConfirmationToken(null);
            orderRepository.save(order);
        } else {
            throw new IllegalStateException("This order has already been confirmed.");
        }
    }

}
