package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.mappers.AddressMapper;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.Address;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.dto.address.AddressCreate;
import fatecipi.progweb.mymanga.dto.address.AddressResponse;
import fatecipi.progweb.mymanga.dto.address.CepResult;
import fatecipi.progweb.mymanga.repositories.AddressRepository;
import fatecipi.progweb.mymanga.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressMapper addressMapper;
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AddressService addressService;

    private Users user;
    private Address address;
    private CepResult cepResult;
    private AddressCreate addressCreate;
    private String username;
    private AddressResponse addressResponse;

    @BeforeEach
    void setUp() {
        username = "test123";
        user = Users.builder()
                .id(1L)
                .email("email@email.com")
                .username("test123")
                .name("Test")
                .password("password")
                .createdAt(Instant.now())
                .isActive(true)
                .confirmationToken(null)
                .address(null)
                .roles(null)
                .orders(null)
                .build();

        cepResult = CepResult.builder()
                .cep("01001-000")
                .logradouro("Praça da Sé")
                .complemento("lado ímpar")
                .unidade("")
                .bairro("Sé")
                .localidade("São Paulo")
                .uf("SP")
                .estado("São Paulo")
                .regiao("Sudeste")
                .ibge("3550308")
                .gia("1004")
                .ddd("11")
                .siafi("7107")
                .erro(false)
                .build();
        addressCreate = AddressCreate.builder()
                .cep("01001000")
                .number("")
                .complement("lado ímpar")
                .build();
        address = Address.builder()
                .users(user)
                .cep(cepResult.cep())
                .street(cepResult.logradouro())
                .number(addressCreate.number())
                .complement(addressCreate.complement())
                .locality(cepResult.bairro())
                .city(cepResult.localidade())
                .state(cepResult.estado())
                .build();

        addressResponse = new AddressResponse(
                1L,
                cepResult.cep(),
                cepResult.logradouro(),
                addressCreate.number(),
                addressCreate.complement(),
                cepResult.bairro(),
                cepResult.localidade(),
                cepResult.estado()
        );
    }

    @Nested
    class addNewAddressToUser {
        @Test
        @DisplayName("should return a AddressResponse when everything is ok")
        void addNewAddressToUser_returnAddressResponse_whenEverythingIsOk() {
            doReturn(Optional.of(user)).when(userRepository).findByUsername(anyString());
            doReturn(cepResult).when(restTemplate).getForObject(anyString(), eq(CepResult.class));
            doReturn(address).when(addressRepository).save(any(Address.class));
            doReturn(addressResponse).when(addressMapper).toAddressResponse(any(Address.class));

            var output = addressService.addNewAddressToUser(username, addressCreate);

            assertNotNull(output);
            assertEquals(cepResult.logradouro(), output.street());
            verify(addressRepository, times(1)).save(any(Address.class));
            verify(addressMapper, times(1)).toAddressResponse(any(Address.class));
            verify(restTemplate, times(1)).getForObject(anyString(), eq(CepResult.class));
            verify(userRepository, times(1)).findByUsername(username);
        }

        @Test
        @DisplayName("should throw a ResourceNotFoundException when the Address isn't found")
        void addNewAddressToUser_throwResourceNotFoundException_whenAddressIsntFound() {
            CepResult errorCepResult = CepResult.builder()
                    .erro(true)
                    .build();

            doReturn(Optional.of(user)).when(userRepository).findByUsername(anyString());
            doReturn(errorCepResult).when(restTemplate).getForObject(anyString(), eq(CepResult.class));

            assertThrows(ResourceNotFoundException.class, () -> addressService.addNewAddressToUser("test123", addressCreate));
        }

        @Test
        @DisplayName("should throw a IllegalArgumentException when the Cep length is different than 8")
        void addNewAddressToUser_throwIllegalArgumentException_whenCepLengthIsDifferentThan8() {
            AddressCreate errorCep = new AddressCreate(
                    "0100100000",
                    "",
                    "lado ímpar"
            );
            assertThrows(IllegalArgumentException.class, () -> addressService.addNewAddressToUser(username, errorCep));
        }

        @Test
        @DisplayName("should throw a ResourceNotFoundException when User isn't found")
        void addNewAddressToUser_throwResourceNotFoundException_whenUserIsntFound() {
            doReturn(Optional.empty()).when(userRepository).findByUsername(anyString());

            assertThrows(ResourceNotFoundException.class, () -> addressService.addNewAddressToUser("test123", addressCreate));
        }
    }

    //TODO: concluir testes da classe de endereço
}