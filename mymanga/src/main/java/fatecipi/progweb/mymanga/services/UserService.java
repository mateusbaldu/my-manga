package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.Manga;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.models.dtos.UserDto;
import fatecipi.progweb.mymanga.models.mappers.UserMapper;
import fatecipi.progweb.mymanga.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    public List<Users> findAll() {
        return userRepository.findAll();
    }

    public Users findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User with email"+ email +"not found"));
    }

    public void deleteByEmail(String email) {
        userRepository.delete(findByEmail(email));
    }

    public Users update(UserDto userDto, String email) {
        Users user = findByEmail(email);
        userMapper.mapUser(userDto, user);
        return user;
    }

    
}
