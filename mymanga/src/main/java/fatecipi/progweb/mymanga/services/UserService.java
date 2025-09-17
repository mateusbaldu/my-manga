package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.dto.user.UserCreateDto;
import fatecipi.progweb.mymanga.dto.user.UserResponseDto;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.user.UserMapper;
import fatecipi.progweb.mymanga.models.user.Users;
import fatecipi.progweb.mymanga.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    public List<UserResponseDto> findAll() {
        List<Users> users = userRepository.findAll();
        return users.stream()
                .map(user -> userMapper.toUserResponseDto(user)
                ).collect(Collectors.toList());
    }

    public Users findByEmailWithoutDto(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User with email"+ email +"not found"));
    }

    public UserResponseDto findByEmail(String email) {
        Users user = findByEmailWithoutDto(email);
        return userMapper.toUserResponseDto(user);
    }

    public void deleteByEmail(String email) {
        userRepository.delete(findByEmailWithoutDto(email));
    }

    public UserResponseDto update(UserCreateDto userCreateDto, String email) {
        Users user = findByEmailWithoutDto(email);
        userMapper.mapCreateUser(userCreateDto, user);
        return userMapper.toUserResponseDto(user);
    }
}
