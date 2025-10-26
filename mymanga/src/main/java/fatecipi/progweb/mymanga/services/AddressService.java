package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.mappers.AddressMapper;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.Address;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.models.dto.address.AddressCreate;
import fatecipi.progweb.mymanga.models.dto.address.AddressResponse;
import fatecipi.progweb.mymanga.models.dto.address.AddressUpdate;
import fatecipi.progweb.mymanga.models.dto.address.CepResult;
import fatecipi.progweb.mymanga.repositories.AddressRepository;
import fatecipi.progweb.mymanga.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final AddressMapper addressMapper;

    public AddressResponse addNewAddressToUser(String username, AddressCreate dto) {
        if (dto.cep().length() != 8) {
            throw new IllegalArgumentException("Cep length must be 8");
        }
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User " + username + " not found"));

        String viaCepUrl = "https://viacep.com.br/ws/" + dto.cep() + "/json/";
        CepResult cepResult = restTemplate.getForObject(viaCepUrl, CepResult.class);

        if (cepResult == null || cepResult.erro()) {
            throw new ResourceNotFoundException("Address with cep " + dto.cep() + " not found");
        }

        Address address = Address.builder()
                .users(user)
                .cep(dto.cep())
                .street(cepResult.logradouro())
                .number(dto.number())
                .complement(dto.complement())
                .locality(cepResult.bairro())
                .city(cepResult.localidade())
                .state(cepResult.estado())
                .build();
        Address savedAddress = addressRepository.save(address);

        return addressMapper.toAddressResponse(savedAddress);
    }

    public AddressResponse getAddressResponseById(String username, Long addressid) {
        Address a = getAddressAssociatedWithUser(username, addressid);
        return addressMapper.toAddressResponse(a);
    }

    public Address getAddressById(Long addressid) {
        return addressRepository.findById(addressid).orElseThrow(() -> new ResourceNotFoundException("Address with id " + addressid + " not found"));
    }

    public void deleteAddressById(String username, Long addressid) {
        Address a = getAddressAssociatedWithUser(username, addressid);
        addressRepository.delete(a);
    }

    public Page<AddressResponse> getUserAddresses(String username, Pageable pageable) {
        return addressRepository.findByUsers_Username(username, pageable).map(addressMapper::toAddressResponse);
    }

    public AddressResponse updateAddressById(String username, Long addressid, AddressUpdate dto) {
        Address address = getAddressAssociatedWithUser(username, addressid);
        addressMapper.updateMapping(dto, address);
        addressRepository.save(address);

        return addressMapper.toAddressResponse(address);
    }

    public Address getAddressAssociatedWithUser(String username, Long addressid) {
        Address address = getAddressById(addressid);
        if (!address.getUsers().getUsername().equals(username)) {
            throw new IllegalArgumentException("User " + username + " is not associated with address with id " + addressid);
        }
        return address;
    }
}
