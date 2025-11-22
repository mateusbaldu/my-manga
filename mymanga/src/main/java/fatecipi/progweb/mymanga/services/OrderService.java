package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.dto.order.OrderCreate;
import fatecipi.progweb.mymanga.dto.order.OrderItemsCreate;
import fatecipi.progweb.mymanga.dto.order.OrderResponse;
import fatecipi.progweb.mymanga.exceptions.NotAvailableException;
import fatecipi.progweb.mymanga.exceptions.PermissionDeniedException;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.listeners.OrderCreatedEvent;
import fatecipi.progweb.mymanga.mappers.OrderMapper;
import fatecipi.progweb.mymanga.models.Order;
import fatecipi.progweb.mymanga.models.OrderItems;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.models.Volume;
import fatecipi.progweb.mymanga.models.enums.OrderStatus;
import fatecipi.progweb.mymanga.repositories.OrderRepository;
import fatecipi.progweb.mymanga.repositories.UserRepository;
import fatecipi.progweb.mymanga.repositories.VolumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public Page<OrderResponse> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable).map(orderMapper::toOrderResponse);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderResponseById(Long id) {
       Order order = getOrderById(id);
        return orderMapper.toOrderResponse(order);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> findAllByUserUsername(String username, Pageable pageable) {
        if (!userRepository.existsByUsername(username)) {
            throw new ResourceNotFoundException("User with username " + username + " not found");
        }
        Page<Order> orderPage = orderRepository.findByUsers_Username(username, pageable);
        return orderPage.map(orderMapper::toOrderResponse);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> findAllByUserId(Long id, Pageable pageable) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }
        Page<Order> orderPage = orderRepository.findByUsers_Id(id, pageable);
        return orderPage.map(orderMapper::toOrderResponse);
    }

    @Transactional
    public void cancelOrder(Long id) {
        Order order = getOrderById(id);

        if (!(order.getStatus() == OrderStatus.CONFIRMED || order.getStatus() == OrderStatus.WAITING_CONFIRMATION)) {
            throw new NotAvailableException("This order can't be cancelled (Status: " + order.getStatus() + ")");
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Transactional
    public OrderResponse update(Long id, OrderCreate orderDto) {
        Order order = getOrderById(id);

        order.getItems().forEach(item -> volumeRepository.findById(item.getVolumeId()).ifPresent(volume -> {
            volume.setQuantity(volume.getQuantity() + item.getQuantity());
            volumeRepository.save(volume);
        }));
        order.getItems().clear();
        orderRepository.flush();

        List<OrderItems> newItems = processOrderItems(orderDto.items(), order);
        order.getItems().addAll(newItems);
        order.setPaymentMethod(orderDto.paymentMethod());

        order.calculateFinalPrice();

        orderRepository.save(order);
        return orderMapper.toOrderResponse(order);
    }

    @Transactional
    public OrderResponse create(OrderCreate orderDto, Users user) {
        if (!user.isActive()) {
            throw new PermissionDeniedException("User account isn't active");
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
        newOrder.setItems(processedItems);

        newOrder.calculateFinalPrice();

        orderRepository.save(newOrder);

        eventPublisher.publishEvent(new OrderCreatedEvent(newOrder));

        return orderMapper.toOrderResponse(newOrder);
    }

    @Transactional
    public void confirmOrder(String token) {
        Order order = orderRepository.findByConfirmationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired confirmation token"));
        if (order.getStatus() != OrderStatus.WAITING_CONFIRMATION) {
            throw new NotAvailableException("This order has already been confirmed.");
        }
        order.setStatus(OrderStatus.CONFIRMED);
        order.setConfirmationToken(null);
        orderRepository.save(order);
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

                     OrderItems items = OrderItems.builder()
                            .volumeId(volume.getId())
                            .title(volume.getManga().getTitle() + " Vol. " + volume.getVolumeNumber())
                            .quantity(itemDto.quantity())
                            .unitPrice(volume.getPrice())
                            .order(order)
                            .build();

                     items.calculateTotalPrice();

                    return items;
                })
                .toList();
    }
}
