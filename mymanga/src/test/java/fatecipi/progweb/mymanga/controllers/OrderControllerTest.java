package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.models.Order;
import fatecipi.progweb.mymanga.models.Role;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.models.dto.order.OrderCreate;
import fatecipi.progweb.mymanga.models.dto.order.OrderItemsCreate;
import fatecipi.progweb.mymanga.models.dto.order.OrderItemsResponse;
import fatecipi.progweb.mymanga.models.dto.order.OrderResponse;
import fatecipi.progweb.mymanga.models.enums.OrderStatus;
import fatecipi.progweb.mymanga.models.enums.PaymentMethod;
import fatecipi.progweb.mymanga.services.OrderService;
import fatecipi.progweb.mymanga.services.UserService;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@WebMvcTest(controllers = OrderController.class)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private OrderService orderService;
    @MockitoBean
    private UserService userService;

    private OrderCreate orderCreate;
    private OrderResponse orderResponse;
    private Order order;
    private Users user;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setId(1L);
        role.setName("BASIC");
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);

        user = new Users(
                1L,
                "email@email.com",
                "test123",
                "Test",
                "password",
                Instant.now(),
                true,
                null,
                null,
                roleSet,
                null
        );

        RestAssuredMockMvc.mockMvc(mockMvc);

        OrderItemsCreate orderItemsCreate = new OrderItemsCreate(
                1L,
                1
        );
        List<OrderItemsCreate> createList = new ArrayList<>();
        createList.add(orderItemsCreate);

        orderCreate = new OrderCreate(
                PaymentMethod.CREDIT,
                createList
        );

        OrderItemsResponse orderItemsResponse = new OrderItemsResponse(
                "Test",
                1,
                1,
                BigDecimal.TEN
        );
        List<OrderItemsResponse> responseList = new ArrayList<>();
        responseList.add(orderItemsResponse);

        orderResponse = new OrderResponse(
                1L,
                Instant.now(),
                BigDecimal.TEN,
                PaymentMethod.CREDIT,
                OrderStatus.WAITING_CONFIRMATION,
                responseList,
                user
        );

        order = new Order(
                1L,
                Instant.now(),
                BigDecimal.TEN,
                null,
                PaymentMethod.CREDIT,
                OrderStatus.WAITING_CONFIRMATION,
                null,
                user
        );
    }

    @Nested
    class createOrder {
        @Test
        @DisplayName("POST /my-manga/orders/new - should return a Order Response" +
                "when everything is ok")
        void createOrder_returnOrderResponse_whenEverythingIsOk() {
            doReturn(user).when(userService).getUserById(anyLong());
            doReturn(orderResponse).when(orderService).create(any(OrderCreate.class), any(Users.class));

            RestAssuredMockMvc
                    .given()
                    .contentType(ContentType.JSON)
                    .body(orderCreate)
                    .postProcessors(
                            jwt().jwt(j -> j.subject(user.getId().toString())),
                            csrf()
                    )
                    .when()
                    .post("/my-manga/orders/new")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("items[0].mangaTitle", equalTo("Test"));
            verify(userService, times(1)).getUserById(anyLong());
            verify(orderService, times(1)).create(orderCreate, user);
        }
    }

    @Nested
    class getOrderById {
        @Test
        @DisplayName("GET /my-manga/orders/{id} - should return Order Response" +
                "when everything is ok")
        void getOrderById_returnOrderResponse_whenEverythingIsOk() {
            doReturn(orderResponse).when(orderService).getOrderResponseById(anyLong());

            RestAssuredMockMvc
                    .given()
                    .contentType(ContentType.JSON)
                    .body(orderCreate)
                    .postProcessors(
                            jwt().jwt(j -> j.subject(user.getId().toString())),
                            csrf()
                    )
                    .when()
                    .get("/my-manga/orders/{id}", 1L)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("items[0].mangaTitle", equalTo("Test"));
            verify(orderService, times(1)).getOrderResponseById(anyLong());
        }
    }

    @Nested
    class findByUserUsername {
        @Test
        @DisplayName("GET /my-manga/orders/user/{username} - should return Page of Order Response" +
                "when everything is ok")
        void findByUserUsername_returnOrderResponse_whenEverythingIsOk() {
            Page<OrderResponse> pageResponse = new PageImpl<>(List.of(orderResponse), PageRequest.of(0, 10), 1);
            doReturn(pageResponse).when(orderService).findAllByUserUsername(anyString(), any(Pageable.class));

            RestAssuredMockMvc
                    .given()
                    .postProcessors(
                            jwt().jwt(j -> j.subject(user.getId().toString())),
                            csrf()
                    )
                    .when()
                    .get("/my-manga/orders/user/{username}", "Test")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("content[0].items[0].mangaTitle", equalTo("Test"));
            verify(orderService, times(1)).findAllByUserUsername(eq("Test"), any(Pageable.class));
        }
    }

    @Nested
    class deleteById {
        @Test
        @DisplayName("DELETE /my-manga/orders/{id} - should return No Content when everything is ok")
        void deleteById_returnNoContent_whenEverythingIsOk() {
            doReturn(order).when(orderService).getOrderById(anyLong());
            doReturn(user).when(userService).getUserById(anyLong());
            doNothing().when(orderService).delete(anyLong());

            RestAssuredMockMvc
                    .given()
                    .postProcessors(
                            jwt().jwt(j -> j.subject(user.getId().toString())),
                            csrf()
                    )
                    .when()
                    .delete("/my-manga/orders/{id}", 1L)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
            verify(orderService, times(1)).getOrderById(1L);
            verify(userService, times(1)).getUserById(user.getId());
            verify(orderService, times(1)).delete(user.getId());
        }

        @Test
        @DisplayName("DELETE /my-manga/orders/{id} - should throw Exception when User isn't active")
        void deleteById_throwException_whenUserIsNotActive() {
            user.setActive(false);
            doReturn(order).when(orderService).getOrderById(anyLong());
            doReturn(user).when(userService).getUserById(anyLong());

            RestAssuredMockMvc
                    .given()
                    .postProcessors(
                            jwt().jwt(j -> j.subject(user.getId().toString())),
                            csrf()
                    )
                    .when()
                    .delete("/my-manga/orders/{id}", 1L)
                    .then()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            verify(orderService, times(1)).getOrderById(1L);
            verify(userService, times(1)).getUserById(user.getId());
        }

        @Test
        @DisplayName("DELETE /my-manga/orders/{id} - should throw Exception when User dont match the Order User")
        void deleteById_throwException_whenUserDontMatchOrderUser() {
            Users wrongUser = new Users(2L, null, null, null, null, null, true, null, null, null, null);
            order.setUsers(wrongUser);

            doReturn(order).when(orderService).getOrderById(anyLong());
            doReturn(user).when(userService).getUserById(anyLong());

            RestAssuredMockMvc
                    .given()
                    .postProcessors(
                            jwt().jwt(j -> j.subject(user.getId().toString())),
                            csrf()
                    )
                    .when()
                    .delete("/my-manga/orders/{id}", 1L)
                    .then()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            verify(orderService, times(1)).getOrderById(1L);
            verify(userService, times(1)).getUserById(user.getId());
        }
    }

    @Nested
    class update {
        @Test
        @DisplayName("PUT /my-manga/orders/{id} - should return Order Response when everything is ok")
        void update_returnOrderResponse_whenEverythingIsOk() {
            doReturn(orderResponse).when(orderService).update(anyLong(), any(OrderCreate.class));
            doReturn(order).when(orderService).getOrderById(anyLong());
            doReturn(user).when(userService).getUserById(anyLong());

            RestAssuredMockMvc
                    .given()
                    .contentType(ContentType.JSON)
                    .body(orderCreate)
                    .postProcessors(
                            jwt().jwt(j -> j.subject(user.getId().toString())),
                            csrf()
                    )
                    .when()
                    .put("/my-manga/orders/{id}", 1L)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("items[0].mangaTitle", equalTo("Test"));
            verify(orderService, times(1)).update(1L, orderCreate);
        }

        @Test
        @DisplayName("PUT /my-manga/orders/{id} - should throw Exception when User isn't active")
        void update_throwException_whenUserDontMatchOrderUser() {
            user.setActive(false);
            doReturn(order).when(orderService).getOrderById(anyLong());
            doReturn(user).when(userService).getUserById(anyLong());

            RestAssuredMockMvc
                    .given()
                    .contentType(ContentType.JSON)
                    .body(orderCreate)
                    .postProcessors(
                            jwt().jwt(j -> j.subject(user.getId().toString())),
                            csrf()
                    )
                    .when()
                    .put("/my-manga/orders/{id}", 1L)
                    .then()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            verify(orderService, times(1)).getOrderById(1L);
            verify(userService, times(1)).getUserById(user.getId());
        }

        @Test
        @DisplayName("PUT /my-manga/orders/{id} - should throw Exception when User dont match the Order User")
        void update_throwException_whenUserIsNotActive() {
            Users wrongUser = new Users(2L, null, null, null, null, null, true, null, null, null, null);
            order.setUsers(wrongUser);

            doReturn(order).when(orderService).getOrderById(anyLong());
            doReturn(user).when(userService).getUserById(anyLong());

            RestAssuredMockMvc
                    .given()
                    .contentType(ContentType.JSON)
                    .body(orderCreate)
                    .postProcessors(
                            jwt().jwt(j -> j.subject(user.getId().toString())),
                            csrf()
                    )
                    .when()
                    .put("/my-manga/orders/{id}", 1L)
                    .then()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            verify(orderService, times(1)).getOrderById(1L);
            verify(userService, times(1)).getUserById(user.getId());
        }
    }

    @Nested
    class confirmOrder {
        @Test
        @DisplayName("GET /my-manga/orders/confirm - should return a Response Entity with a message" +
                "when everything is ok")
        void confirmOrder_returnMessage_whenEverythingIsOk() {
            String token = UUID.randomUUID().toString();
            doNothing().when(orderService).confirmOrder(anyString());

            RestAssuredMockMvc
                    .given()
                    .param("token", token)
                    .postProcessors(
                            jwt().jwt(j -> j.subject(user.getId().toString())),
                            csrf())
                    .when()
                    .get("/my-manga/orders/confirm")
                    .then()
                    .statusCode(HttpStatus.OK.value());
            verify(orderService, times(1)).confirmOrder(token);
        }
    }

    @Nested
    class listAll {
        @Test
        @DisplayName("GET /my-manga/orders/all - return Page of Order Response when everything is ok")
        void listAll_returnPageOrderResponse_whenEverythingIsOk() {
            Page<OrderResponse> pageResponse = new PageImpl<>(List.of(orderResponse), PageRequest.of(0, 10), 1);
            doReturn(pageResponse).when(orderService).findAll(any(Pageable.class));

            RestAssuredMockMvc
                    .given()
                    .postProcessors(
                            jwt().jwt(j -> j.subject(user.getId().toString())),
                            csrf()
                    )
                    .when()
                    .get("/my-manga/orders/all")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("totalElements", equalTo(1))
                    .body("totalPages", equalTo(1))
                    .body("content[0].items[0].mangaTitle", equalTo("Test"));
            verify(orderService, times(1)).findAll(any(Pageable.class));
        }
    }
}