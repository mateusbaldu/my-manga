package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.models.dto.address.AddressCreate;
import fatecipi.progweb.mymanga.models.dto.address.AddressResponse;
import fatecipi.progweb.mymanga.repositories.AddressRepository;
import fatecipi.progweb.mymanga.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AddressService {
    private AddressRepository addressRepository;
    private UserRepository userRepository;

    public AddressResponse addNewAddressToUser(Long userid, AddressCreate dto) {
        Users user = userRepository.findById(userid)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userid + " not found"));
    }
}
