package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.configs.mappers.OrderMapper;
import fatecipi.progweb.mymanga.enums.Genres;
import fatecipi.progweb.mymanga.enums.MangaStatus;
import fatecipi.progweb.mymanga.enums.OrderStatus;
import fatecipi.progweb.mymanga.enums.PaymentMethod;
import fatecipi.progweb.mymanga.exceptions.NotAvailableException;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.*;
import fatecipi.progweb.mymanga.models.dto.order.OrderCreate;
import fatecipi.progweb.mymanga.models.dto.order.OrderItemsCreate;
import fatecipi.progweb.mymanga.models.dto.order.OrderItemsResponse;
import fatecipi.progweb.mymanga.models.dto.order.OrderResponse;
import fatecipi.progweb.mymanga.repositories.OrderRepository;
import fatecipi.progweb.mymanga.repositories.UserRepository;
import fatecipi.progweb.mymanga.repositories.VolumeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private MangaService mangaService;
    @Mock
    private EmailService emailService;
    @Mock
    private VolumeRepository volumeRepository;
    @InjectMocks
    private OrderService orderService;


    @Nested
    class findAll {
        @Test
        @DisplayName("should return a Page of Orders when everything is ok")
        void findAll_returnPageOrder_whenEverythingIsOk() {
            Order order = new Order(
                    1L,
                    Instant.now(),
                    BigDecimal.valueOf(20.00),
                    UUID.randomUUID().toString(),
                    PaymentMethod.CREDIT,
                    OrderStatus.WAITING_CONFIRMATION,
                    null,
                    null
            );
            OrderResponse orderResponse = new OrderResponse(
                    1L,
                    Instant.now(),
                    BigDecimal.valueOf(20.00),
                    PaymentMethod.CREDIT,
                    OrderStatus.WAITING_CONFIRMATION,
                    null,
                    null
            );

            Page<Order> pageOrder = new PageImpl<>(List.of(order));
            Pageable pageable = PageRequest.of(0, 10);

            doReturn(pageOrder).when(orderRepository).findAll(any(Pageable.class));
            doReturn(orderResponse).when(orderMapper).toOrderResponse(any(Order.class));

            var output = orderService.findAll(pageable);

            assertNotNull(output);
            verify(orderRepository, times(1)).findAll(pageable);
            verify(orderMapper, times(1)).toOrderResponse(order);
        }
    }

    @Nested
    class getOrderById {
        @Test
        @DisplayName("should return a Order when everything is ok")
        void getOrderById_returnOrder_whenEverythingIsOk() {
            Order order = new Order(
                    1L,
                    Instant.now(),
                    BigDecimal.valueOf(20.00),
                    UUID.randomUUID().toString(),
                    PaymentMethod.CREDIT,
                    OrderStatus.WAITING_CONFIRMATION,
                    null,
                    null
            );

            doReturn(Optional.of(order)).when(orderRepository).findById(1L);

            var output = orderService.getOrderById(1L);

            assertNotNull(output);
            verify(orderRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when Order don't exists")
        void getOrderById_throwResourceNotFoundException_whenOrderDontExists() {
            doReturn(Optional.empty()).when(orderRepository).findById(anyLong());

            assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(1L));
        }
    }

    @Nested
    class getOrderResponseById {
        @Test
        @DisplayName("should return a OrderResponse when everything is ok")
        void getOrderResponseById_returnOrder_whenEverythingIsOk() {
            Order order = new Order(
                    1L,
                    Instant.now(),
                    BigDecimal.valueOf(20.00),
                    UUID.randomUUID().toString(),
                    PaymentMethod.CREDIT,
                    OrderStatus.WAITING_CONFIRMATION,
                    null,
                    null
            );
            OrderResponse orderResponse = new OrderResponse(
                    1L,
                    Instant.now(),
                    BigDecimal.valueOf(20.00),
                    PaymentMethod.CREDIT,
                    OrderStatus.WAITING_CONFIRMATION,
                    null,
                    null
            );


            doReturn(Optional.of(order)).when(orderRepository).findById(1L);
            doReturn(orderResponse).when(orderMapper).toOrderResponse(any(Order.class));

            var output = orderService.getOrderResponseById(1L);

            assertNotNull(output);
            verify(orderRepository, times(1)).findById(1L);
            verify(orderMapper, times(1)).toOrderResponse(order);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when Order don't exists")
        void getOrderResponseById_throwResourceNotFoundException_whenOrderDontExists() {
            doReturn(Optional.empty()).when(orderRepository).findById(anyLong());

            assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderResponseById(1L));
        }
    }

    @Nested
    class findAllByUserUsername {
        @Test
        @DisplayName("should return a Page of OrderResponse when everything is ok")
        void findAllByUserUsername_returnPageOrderResponse_whenEverythingIsOk() {
            String username = "test";

            Order order = new Order(
                    1L,
                    Instant.now(),
                    BigDecimal.valueOf(20.00),
                    UUID.randomUUID().toString(),
                    PaymentMethod.CREDIT,
                    OrderStatus.WAITING_CONFIRMATION,
                    null,
                    null
            );
            OrderResponse orderResponse = new OrderResponse(
                    1L,
                    Instant.now(),
                    BigDecimal.valueOf(20.00),
                    PaymentMethod.CREDIT,
                    OrderStatus.WAITING_CONFIRMATION,
                    null,
                    null
            );

            Page<Order> pageOrder = new PageImpl<>(List.of(order));
            Pageable pageable = PageRequest.of(0, 10);

            doReturn(true).when(userRepository).existsByUsername(anyString());
            doReturn(pageOrder).when(orderRepository).findByUsers_Username(anyString(), any(Pageable.class));
            doReturn(orderResponse).when(orderMapper).toOrderResponse(any(Order.class));

            var output = orderService.findAllByUserUsername(username, pageable);

            assertNotNull(output);
            verify(orderRepository, times(1)).findByUsers_Username(username, pageable);
            verify(orderMapper, times(1)).toOrderResponse(order);
            verify(userRepository, times(1)).existsByUsername(username);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when User don't exists")
        void findAllByUserUsername_throwResourceNotFoundException_whenUserDontExists() {
            Pageable pageable = PageRequest.of(0, 10);
            doReturn(false).when(userRepository).existsByUsername(anyString());

            assertThrows(ResourceNotFoundException.class, () -> orderService.findAllByUserUsername("test123", pageable));
        }
    }

    @Nested
    class delete {
        @Test
        @DisplayName("should return void when everything is ok")
        void delete_void_whenEverythingIsOk() {
            Order order = new Order(
                    1L,
                    Instant.now(),
                    BigDecimal.valueOf(20.00),
                    UUID.randomUUID().toString(),
                    PaymentMethod.CREDIT,
                    OrderStatus.WAITING_CONFIRMATION,
                    null,
                    null
            );

            doReturn(Optional.of(order)).when(orderRepository).findById(1L);
            doNothing().when(orderRepository).delete(order);

            orderService.delete(1L);

            verify(orderRepository, times(1)).findById(1L);
            verify(orderRepository, times(1)).delete(order);
        }

        @Test
        @DisplayName("should throw a ResourceNotFoundException when the Order isn't found")
        void delete_throwResourceNotFoundException_whenOrderIsNotFound() {
            doReturn(Optional.empty()).when(orderRepository).findById(anyLong());

            assertThrows(ResourceNotFoundException.class, () -> orderService.delete(1L));
        }
    }

    @Nested
    class create {
        @Test
        @DisplayName("should return a OrderResponse when everything is ok")
        void create_returnOrderResponse_whenEverythingIsOk() {
            Role role = new Role();
            role.setId(1L);
            role.setName("BASIC");
            Users user = new Users(
                    1L,
                    "email@email.com",
                    "test123",
                    "Test",
                    "password",
                    Instant.now(),
                    true,
                    null,
                    null,
                    Set.of(role),
                    null
            );
            Manga manga = new Manga(
                    1L,
                    "Test",
                    "Author",
                    "Test description",
                    8.5,
                    "test",
                    MangaStatus.COMPLETED,
                    Genres.ACTION,
                    null
            );
            Volume volume = new Volume(
                    1L,
                    1,
                    BigDecimal.valueOf(10.50),
                    "1 to 10",
                    LocalDate.now(),
                    50,
                    manga
            );
            manga.setVolume(Set.of(volume));

            Order order = new Order(
                    1L,
                    Instant.now(),
                    BigDecimal.valueOf(105.00),
                    UUID.randomUUID().toString(),
                    PaymentMethod.CREDIT,
                    OrderStatus.WAITING_CONFIRMATION,
                    null,
                    user
            );
            OrderItems orderItems = new OrderItems(
                    1L,
                    1L,
                    manga.getTitle(),
                    volume.getPrice(),
                    BigDecimal.valueOf(105.00),
                    10,
                    order
            );
            order.setItems(List.of(orderItems));

            List<OrderItemsCreate> orderItemsCreateList = new ArrayList<>();
            OrderItemsCreate orderItemsCreate = new OrderItemsCreate(
                    1L,
                    10
            );
            orderItemsCreateList.add(orderItemsCreate);
            OrderCreate orderCreate = new OrderCreate(
                    PaymentMethod.CREDIT,
                    orderItemsCreateList
            );

            OrderItemsResponse orderItemsResponse = new OrderItemsResponse(
                    manga.getTitle(),
                    volume.getVolumeNumber(),
                    orderItemsCreate.quantity(),
                    volume.getPrice()
                    );
            OrderResponse orderResponse = new OrderResponse(
                    1L,
                    Instant.now(),
                    BigDecimal.valueOf(105.00),
                    PaymentMethod.CREDIT,
                    OrderStatus.WAITING_CONFIRMATION,
                    List.of(orderItemsResponse),
                    user
            );

            doReturn(volume).when(mangaService).getVolumeResponseById(anyLong());
            doReturn(volume).when(volumeRepository).save(any(Volume.class));
            doReturn(order).when(orderRepository).save(any(Order.class));
            doReturn(orderResponse).when(orderMapper).toOrderResponse(any(Order.class));

            var output = orderService.create(orderCreate, user);

            assertNotNull(output);
            assertEquals(orderCreate.paymentMethod(), output.paymentMethod());
            verify(orderRepository, times(1)).save(any(Order.class));
            verify(volumeRepository, times(1)).save(any(Volume.class));
            verify(orderMapper, times(1)).toOrderResponse(any(Order.class));
            verify(mangaService, times(1)).getVolumeResponseById(1L);
        }

        @Test
        @DisplayName("should throw NotAvailableException when the OrderCreateDto has more items than the available on the system")
        void create_throwNotAvailableException_whenItemsArentAvailable() {
            Users user = new Users(
                    1L,
                    "email@email.com",
                    "test123",
                    "Test",
                    "password",
                    Instant.now(),
                    true,
                    null,
                    null,
                    null,
                    null
            );
            Manga manga = new Manga(
                    1L,
                    "Test",
                    "Author",
                    "Test description",
                    8.5,
                    "test",
                    MangaStatus.COMPLETED,
                    Genres.ACTION,
                    null
            );
            Volume volume = new Volume(
                    1L,
                    1,
                    BigDecimal.valueOf(10.50),
                    "1 to 10",
                    LocalDate.now(),
                    10,
                    manga
            );
            manga.setVolume(Set.of(volume));
            List<OrderItemsCreate> orderItemsCreateList = new ArrayList<>();
            OrderItemsCreate orderItemsCreate = new OrderItemsCreate(
                    1L,
                    50
            );
            orderItemsCreateList.add(orderItemsCreate);
            OrderCreate orderCreate = new OrderCreate(
                    PaymentMethod.CREDIT,
                    orderItemsCreateList
            );

            doReturn(volume).when(mangaService).getVolumeResponseById(anyLong());

            assertThrows(NotAvailableException.class, () -> orderService.create(orderCreate, user));
        }

        @Test
        @DisplayName("should throw BadCredentialsException when the User isn't active")
        void create_throwBadCredentialsException_whenUserIsNotActive() {
            Users user = new Users(
                    1L,
                    "email@email.com",
                    "test123",
                    "Test",
                    "password",
                    Instant.now(),
                    false,
                    null,
                    null,
                    null,
                    null
            );
            List<OrderItemsCreate> orderItemsCreateList = new ArrayList<>();
            OrderItemsCreate orderItemsCreate = new OrderItemsCreate(
                    1L,
                    10
            );
            orderItemsCreateList.add(orderItemsCreate);
            OrderCreate orderCreate = new OrderCreate(
                    PaymentMethod.CREDIT,
                    orderItemsCreateList
            );

            assertThrows(BadCredentialsException.class, () -> orderService.create(orderCreate, user));
        }
    }

    @Nested
    class confirmOrder {
        @Test
        @DisplayName("should return void when everything is ok")
        void confirmOrder_returnVoid_whenEverythingIsOk() {
            String token = UUID.randomUUID().toString();
            Order order = new Order(
                    1L,
                    Instant.now(),
                    BigDecimal.valueOf(105.00),
                    token,
                    PaymentMethod.CREDIT,
                    OrderStatus.WAITING_CONFIRMATION,
                    null,
                    null
            );

            doReturn(Optional.of(order)).when(orderRepository).findByConfirmationToken(anyString());
            doReturn(order).when(orderRepository).save(any(Order.class));

            orderService.confirmOrder(token);
            verify(orderRepository, times(1)).findByConfirmationToken(token);
            verify(orderRepository, times(1)).save(order);
            assertNull(order.getConfirmationToken());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when the Order isn't found")
        void confirmOrder_throwResourceNotFoundException_whenOrderIsNotFound() {
            String token = UUID.randomUUID().toString();
            doReturn(Optional.empty()).when(orderRepository).findByConfirmationToken(anyString());

            assertThrows(ResourceNotFoundException.class, () -> orderService.confirmOrder(token));
        }

        @Test
        @DisplayName("should throw IllegalStateException when the OrderStatus isn't WAITING_CONFIRMATION")
        void confirmOrder_throwIllegalStateException_whenOrderStatusIsntWAITING_CONFIRMATION() {
            String token = UUID.randomUUID().toString();
            Order order = new Order(
                    1L,
                    Instant.now(),
                    BigDecimal.valueOf(105.00),
                    token,
                    PaymentMethod.CREDIT,
                    OrderStatus.CONFIRMED,
                    null,
                    null
            );

            doReturn(Optional.of(order)).when(orderRepository).findByConfirmationToken(anyString());

            assertThrows(IllegalStateException.class, () -> orderService.confirmOrder(token));
        }
    }

    @Nested
    class update {
        @Test
        @DisplayName("should return a OrderResponse when everything is ok")
        void update_returnOrderResponse_whenEverythingIsOk() {
            Role role = new Role();
            role.setId(1L);
            role.setName("BASIC");
            Users user = new Users(
                    1L,
                    "email@email.com",
                    "test123",
                    "Test",
                    "password",
                    Instant.now(),
                    true,
                    null,
                    null,
                    Set.of(role),
                    null
            );
            Manga manga = new Manga(
                    1L,
                    "Test",
                    "Author",
                    "Test description",
                    8.5,
                    "test",
                    MangaStatus.COMPLETED,
                    Genres.ACTION,
                    null
            );
            Volume volume = new Volume(
                    1L,
                    1,
                    BigDecimal.valueOf(10.50),
                    "1 to 10",
                    LocalDate.now(),
                    50,
                    manga
            );
            manga.setVolume(Set.of(volume));

            Order order = new Order(
                    1L,
                    Instant.now(),
                    BigDecimal.valueOf(105.00),
                    UUID.randomUUID().toString(),
                    PaymentMethod.CREDIT,
                    OrderStatus.WAITING_CONFIRMATION,
                    null,
                    user
            );
            OrderItems orderItems = new OrderItems(
                    1L,
                    1L,
                    manga.getTitle(),
                    volume.getPrice(),
                    BigDecimal.valueOf(105.00),
                    10,
                    order
            );
            List<OrderItems> items = new ArrayList<>();
            items.add(orderItems);
            order.setItems(items);

            List<OrderItemsCreate> orderItemsCreateList = new ArrayList<>();
            OrderItemsCreate orderItemsCreate = new OrderItemsCreate(
                    1L,
                    10
            );
            orderItemsCreateList.add(orderItemsCreate);
            OrderCreate orderCreate = new OrderCreate(
                    PaymentMethod.CREDIT,
                    orderItemsCreateList
            );

            OrderItemsResponse orderItemsResponse = new OrderItemsResponse(
                    manga.getTitle(),
                    volume.getVolumeNumber(),
                    orderItemsCreate.quantity(),
                    volume.getPrice()
            );
            OrderResponse orderResponse = new OrderResponse(
                    1L,
                    Instant.now(),
                    BigDecimal.valueOf(105.00),
                    PaymentMethod.CREDIT,
                    OrderStatus.WAITING_CONFIRMATION,
                    List.of(orderItemsResponse),
                    user
            );

            doReturn(Optional.of(order)).when(orderRepository).findById(anyLong());
            doReturn(volume).when(mangaService).getVolumeResponseById(anyLong());
            doReturn(volume).when(volumeRepository).save(any(Volume.class));
            doReturn(order).when(orderRepository).save(any(Order.class));
            doReturn(orderResponse).when(orderMapper).toOrderResponse(any(Order.class));

            var output = orderService.update(1L, orderCreate);

            assertNotNull(output);
            assertEquals(orderCreate.paymentMethod(), output.paymentMethod());
            verify(orderRepository, times(1)).findById(1L);
            verify(orderRepository, times(1)).save(any(Order.class));
            verify(volumeRepository, times(1)).save(any(Volume.class));
            verify(orderMapper, times(1)).toOrderResponse(any(Order.class));
            verify(mangaService, times(1)).getVolumeResponseById(1L);
        }

        @Test
        @DisplayName("should throw NotAvailableException when the OrderCreateDto has more items than the available on the system")
        void update_throwNotAvailableException_whenItemsArentAvailable() {
            Manga manga = new Manga(
                    1L,
                    "Test",
                    "Author",
                    "Test description",
                    8.5,
                    "test",
                    MangaStatus.COMPLETED,
                    Genres.ACTION,
                    null
            );
            Volume volume = new Volume(
                    1L,
                    1,
                    BigDecimal.valueOf(10.50),
                    "1 to 10",
                    LocalDate.now(),
                    10,
                    manga
            );
            manga.setVolume(Set.of(volume));
            List<OrderItemsCreate> orderItemsCreateList = new ArrayList<>();
            OrderItemsCreate orderItemsCreate = new OrderItemsCreate(
                    1L,
                    50
            );
            orderItemsCreateList.add(orderItemsCreate);
            OrderCreate orderCreate = new OrderCreate(
                    PaymentMethod.CREDIT,
                    orderItemsCreateList
            );
            Order order = new Order(
                    1L,
                    Instant.now(),
                    BigDecimal.valueOf(105.00),
                    UUID.randomUUID().toString(),
                    PaymentMethod.CREDIT,
                    OrderStatus.WAITING_CONFIRMATION,
                    null,
                    null
            );
            OrderItems orderItems = new OrderItems(
                    1L,
                    1L,
                    manga.getTitle(),
                    volume.getPrice(),
                    BigDecimal.valueOf(105.00),
                    10,
                    order
            );
            List<OrderItems> items = new ArrayList<>();
            items.add(orderItems);
            order.setItems(items);

            doReturn(Optional.of(order)).when(orderRepository).findById(anyLong());
            doReturn(volume).when(mangaService).getVolumeResponseById(anyLong());

            assertThrows(NotAvailableException.class, () -> orderService.update(1L, orderCreate));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when Order isn't found")
        void update_throwResourceNotFoundException_whenOrderIsntFound() {
            OrderCreate orderCreate = new OrderCreate(
                    PaymentMethod.CREDIT,
                    null
            );
            doReturn(Optional.empty()).when(orderRepository).findById(anyLong());

            assertThrows(ResourceNotFoundException.class, () -> orderService.update(1L, orderCreate));
        }
    }
}