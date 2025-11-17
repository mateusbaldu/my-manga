package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.dto.address.AddressCreate;
import fatecipi.progweb.mymanga.dto.address.AddressResponse;
import fatecipi.progweb.mymanga.dto.address.AddressUpdate;
import fatecipi.progweb.mymanga.services.AddressService;
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

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@WebMvcTest(AddressController.class)
class AddressControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AddressService addressService;
    @MockitoBean
    private UserService userService;

    private Users user;
    private AddressResponse addressResponse;
    private AddressCreate addressCreate;
    private AddressUpdate addressUpdate;
    private Long addressId;
    private String userUsername;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);

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
                null,
                null
        );
        addressCreate = new AddressCreate(
                "01001000",
                "",
                "lado ímpar"
        );
        addressResponse = new AddressResponse(
                1L,
                "01001000",
                "Praça da Sé",
                "",
                "lado ímpar",
                "Sé",
                "São Paulo",
                "São Paulo"
        );
        addressUpdate = new AddressUpdate(
                "01001000",
                "Praça da Sé",
                "",
                "lado ímpar UPDATED",
                "Sé",
                "São Paulo",
                "São Paulo"
        );
        addressId = 1L;
        userUsername = "test123";
    }

    @Nested
    class addNewAddressToUser {
        @Test
        @DisplayName("POST /my-manga/users/{username}/address/new - should return a Address Response when everything is ok")
        void addNewAddressToUser_returnAddressResponse_whenEverythingIsOk() {
            doReturn(addressResponse).when(addressService).addNewAddressToUser(anyString(), any(AddressCreate.class));
            doReturn(user).when(userService).getUserByUsername(anyString());

            RestAssuredMockMvc
                    .given()
                    .contentType(ContentType.JSON)
                    .body(addressCreate)
                    .postProcessors(
                            jwt().jwt(j -> j.subject(user.getId().toString())),
                            csrf()
                    )
                    .when()
                    .post("/users/{username}/address/new", userUsername)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("cep", equalTo("01001000"));
            verify(addressService, times(1)).addNewAddressToUser(userUsername, addressCreate);
            verify(userService, times(1)).getUserByUsername(userUsername);
        }
    }

    @Nested
    class getAddressById {
        @Test
        @DisplayName("GET /my-manga/users/{username/address/{addressid} - should return Address Response when everything is ok")
        void getAddressById_returnAddressResponse_whenEverythingIsOk() {
            doReturn(addressResponse).when(addressService).getAddressResponseById(anyString(), anyLong());
            doReturn(user).when(userService).getUserByUsername(anyString());

            RestAssuredMockMvc
                    .given()
                    .postProcessors(
                            jwt().jwt(j -> j.subject(user.getId().toString())),
                            csrf()
                    )
                    .when()
                    .get("/users/{username}/address/{addressid}", userUsername, addressId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("cep", equalTo("01001000"));
            verify(addressService, times(1)).getAddressResponseById(userUsername, addressId);
            verify(userService, times(1)).getUserByUsername(userUsername);
        }
    }

    @Nested
    class deleteAddressById {
        @Test
        @DisplayName("DELETE /my-manga/users/{username/address/{addressid} - should return No Content when everything is ok")
        void deleteAddressById_returnNoContent_whenEverythingIsOk() {
            doReturn(user).when(userService).getUserByUsername(anyString());
            doNothing().when(addressService).deleteAddressById(anyString(), anyLong());

            RestAssuredMockMvc
                    .given()
                    .postProcessors(
                            jwt().jwt(j -> j.subject(user.getId().toString())),
                            csrf()
                    )
                    .when()
                    .delete("/users/{username}/address/{addressid}", userUsername, addressId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
            verify(addressService, times(1)).deleteAddressById(userUsername, addressId);
            verify(userService, times(1)).getUserByUsername(userUsername);
        }
    }

    @Nested
    class getAllAddressesFromUser {
        @Test
        @DisplayName("GET /my-manga/users/{username/address/all - should return Page of Address Response when everything is ok")
        void getAllAddressesFromUser_returnPageAddressResponse_whenEverythingIsOk() {
            Page<AddressResponse> pageResponse = new PageImpl<>(List.of(addressResponse), PageRequest.of(0, 10), 1);
            doReturn(pageResponse).when(addressService).getUserAddresses(anyString(), any(Pageable.class));
            doReturn(user).when(userService).getUserByUsername(anyString());

            RestAssuredMockMvc
                    .given()
                    .postProcessors(
                            jwt().jwt(j -> j.subject(user.getId().toString())),
                            csrf()
                    )
                    .when()
                    .get("/users/{username}/address/all", userUsername)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("content[0].cep", equalTo("01001000"))
                    .body("totalElements", equalTo(1));
            verify(addressService, times(1)).getUserAddresses(eq(userUsername), any(Pageable.class));
            verify(userService, times(1)).getUserByUsername(userUsername);
        }
    }

    @Nested
    class updateAddress {
        @Test
        @DisplayName("PATCH /my-manga/users/{username}/address/{addressid} - should return Address Response when everything is ok")
        void updateAddress_returnAddressResponse_whenEverythingIsOk() {
            doReturn(user).when(userService).getUserByUsername(anyString());
            doReturn(addressResponse).when(addressService).updateAddressById(anyString(), anyLong(), any(AddressUpdate.class));

            RestAssuredMockMvc
                    .given()
                    .contentType(ContentType.JSON)
                    .body(addressUpdate)
                    .postProcessors(
                            jwt().jwt(j -> j.subject(user.getId().toString())),
                            csrf()
                    )
                    .when()
                    .patch("/users/{username}/address/{addressid}", userUsername, addressId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("cep", equalTo("01001000"));
            verify(addressService, times(1)).updateAddressById(userUsername, addressId, addressUpdate);
            verify(userService, times(1)).getUserByUsername(userUsername);
        }
    }

    @Nested
    class verifyUserPermission {
        //TODO
    }
}