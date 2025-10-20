package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.mappers.AddressMapper;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.Address;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.models.dto.address.AddressCreate;
import fatecipi.progweb.mymanga.models.dto.address.AddressResponse;
import fatecipi.progweb.mymanga.models.dto.address.CepResult;
import fatecipi.progweb.mymanga.repositories.AddressRepository;
import fatecipi.progweb.mymanga.repositories.UserRepository;
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

    @Nested
    class addNewAddressToUser {
        @Test
        @DisplayName("should return a AddressResponse when everything is ok")
        void addNewAddressToUser_returnAddressResponse_whenEverythingIsOk() {
            String username = "test123";
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
            AddressCreate addressCreate = new AddressCreate(
                    "01001000",
                    "",
                    "lado ímpar"
            );
            CepResult cepResult = new CepResult(
                    "01001-000",
                    "Praça da Sé",
                    "lado ímpar",
                    "",
                    "Sé",
                    "São Paulo",
                    "SP",
                    "São Paulo",
                     "Sudeste",
                     "3550308",
                    "1004",
                    "11",
                    "7107",
                    false
            );
            Address address = Address.builder()
                    .users(user)
                    .cep(cepResult.cep())
                    .street(cepResult.logradouro())
                    .number(addressCreate.number())
                    .complement(addressCreate.complement())
                    .locality(cepResult.bairro())
                    .city(cepResult.localidade())
                    .state(cepResult.estado())
                    .build();

            AddressResponse addressResponse = new AddressResponse(
                    1L,
                    cepResult.cep(),
                    cepResult.logradouro(),
                    addressCreate.number(),
                    addressCreate.complement(),
                    cepResult.bairro(),
                    cepResult.localidade(),
                    cepResult.estado()
            );

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
            AddressCreate addressCreate = new AddressCreate(
                    "01001000",
                    "",
                    "lado ímpar"
            );
            CepResult cepResult = new CepResult(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    true
            );
            doReturn(Optional.of(user)).when(userRepository).findByUsername(anyString());
            doReturn(cepResult).when(restTemplate).getForObject(anyString(), eq(CepResult.class));

            assertThrows(ResourceNotFoundException.class, () -> addressService.addNewAddressToUser("test123", addressCreate));
        }

        @Test
        @DisplayName("should throw a IllegalArgumentException when the Cep length is different than 8")
        void addNewAddressToUser_throwIllegalArgumentException_whenCepLengthIsDifferentThan8() {
            String username = "test123";
            AddressCreate addressCreate = new AddressCreate(
                    "01001000",
                    "",
                    "lado ímpar"
            );
            assertThrows(IllegalArgumentException.class, () -> addressService.addNewAddressToUser(username, addressCreate));
        }

        @Test
        @DisplayName("should throw a ResourceNotFoundException when User isn't found")
        void addNewAddressToUser_throwResourceNotFoundException_whenUserIsntFound() {
            doReturn(Optional.empty()).when(userRepository).findByUsername(anyString());
            AddressCreate addressCreate = new AddressCreate(
                    "01001000",
                    "",
                    "lado ímpar"
            );
            assertThrows(ResourceNotFoundException.class, () -> addressService.addNewAddressToUser("test123", addressCreate));
        }
    }

    //TODO: concluir testes da classe de endereço
}