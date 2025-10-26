package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.exceptions.NotAvailableException;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.mappers.OrderMapper;
import fatecipi.progweb.mymanga.models.*;
import fatecipi.progweb.mymanga.models.dto.order.OrderCreate;
import fatecipi.progweb.mymanga.models.dto.order.OrderItemsCreate;
import fatecipi.progweb.mymanga.models.dto.order.OrderResponse;
import fatecipi.progweb.mymanga.models.enums.OrderStatus;
import fatecipi.progweb.mymanga.repositories.OrderRepository;
import fatecipi.progweb.mymanga.repositories.UserRepository;
import fatecipi.progweb.mymanga.repositories.VolumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final VolumeRepository volumeRepository;
    private final EmailService emailService;
    public Page<OrderResponse> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable).map(orderMapper::toOrderResponse);
    }

    public OrderResponse getOrderResponseById(Long id) {
       Order order = getOrderById(id);
        return orderMapper.toOrderResponse(order);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " not found"));
    }

    public Page<OrderResponse> findAllByUserUsername(String username, Pageable pageable) {
        if (!userRepository.existsByUsername(username)) {
            throw new ResourceNotFoundException("User with username " + username + " not found");
        }
        Page<Order> orderPage = orderRepository.findByUsers_Username(username, pageable);
        return orderPage.map(orderMapper::toOrderResponse);
    }

    public void delete(Long id) {
        orderRepository.delete(getOrderById(id));
    }

    public OrderResponse update(Long id, OrderCreate orderDto) {
        Order order = getOrderById(id);

        order.getItems().forEach(item -> volumeRepository.findById(item.getVolumeId()).ifPresent(volume -> {
            volume.setQuantity(volume.getQuantity() + item.getQuantity());
            volumeRepository.save(volume);
        }));
        order.getItems().clear();
        orderRepository.flush();

        List<OrderItems> newItems = processOrderItems(orderDto.items(), order);
        BigDecimal finalPrice = calculateFinalPrice(newItems, order.getUsers());

        order.getItems().addAll(newItems);
        order.setFinalPrice(finalPrice);
        order.setPaymentMethod(orderDto.paymentMethod());

        orderRepository.save(order);
        return orderMapper.toOrderResponse(order);
    }

    public OrderResponse create(OrderCreate orderDto, Users user) {
        if (!user.isActive()) {
            throw new BadCredentialsException("A conta do usuário não está ativa");
        }

        String token = UUID.randomUUID().toString();

        Order newOrder = Order.builder()
                .users(user)
                .paymentMethod(orderDto.paymentMethod())
                .createdAt(Instant.now())
                .confirmationToken(token)
                .status(OrderStatus.WAITING_CONFIRMATION)
                .build();

        List<OrderItems> processedItems = processOrderItems(orderDto.items(), newOrder);
        BigDecimal finalPrice = calculateFinalPrice(processedItems, user);

        newOrder.setItems(processedItems);
        newOrder.setFinalPrice(finalPrice);

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



    private List<OrderItems> processOrderItems(List<OrderItemsCreate> itemDtos, Order order) {
        return itemDtos.stream()
                .map(itemDto -> {
                    Volume volume = volumeRepository.findById(itemDto.volumeId()).orElseThrow(
                            () -> new ResourceNotFoundException("Volume com id " + itemDto.volumeId() + " não encontrado"));

                    if (volume.getQuantity() < itemDto.quantity()) {
                        throw new NotAvailableException(volume.getManga().getTitle() + " Vol. " + volume.getVolumeNumber() + " não está disponível na quantidade solicitada.");
                    }

                    volume.setQuantity(volume.getQuantity() - itemDto.quantity());
                    volumeRepository.save(volume);

                    return OrderItems.builder()
                            .volumeId(volume.getId())
                            .title(volume.getManga().getTitle() + " Vol. " + volume.getVolumeNumber())
                            .quantity(itemDto.quantity())
                            .unitPrice(volume.getPrice())
                            .totalPrice(volume.getPrice().multiply(BigDecimal.valueOf(itemDto.quantity())))
                            .order(order)
                            .build();
                })
                .toList();
    }

    private BigDecimal calculateFinalPrice(List<OrderItems> items, Users user) {
        BigDecimal totalPrice = items.stream()
                .map(OrderItems::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean isSubscriber = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(Role.Values.SUBSCRIBER.name()));

        if (isSubscriber) {
            return totalPrice.multiply(new BigDecimal("0.80")).setScale(2, RoundingMode.HALF_UP);
        }

        return totalPrice;
    }
}
