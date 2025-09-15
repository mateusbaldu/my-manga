package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.user.UserCreateDto;
import fatecipi.progweb.mymanga.models.user.Users;
import fatecipi.progweb.mymanga.models.user.UserMapper;
import fatecipi.progweb.mymanga.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Users update(UserCreateDto userCreateDto, String email) {
        Users user = findByEmail(email);
        userMapper.mapUser(userCreateDto, user);
        return user;
    }


}
