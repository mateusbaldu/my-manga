package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.order.Order;
import fatecipi.progweb.mymanga.dto.order.OrderCreateDto;
import fatecipi.progweb.mymanga.models.order.OrderMapper;
import fatecipi.progweb.mymanga.repositories.VolumeRepository;
import fatecipi.progweb.mymanga.repositories.OrderRepository;
import fatecipi.progweb.mymanga.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private VolumeRepository volumeRepository;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " not found"));
    }

    public List<Order> findByUserEmail(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new ResourceNotFoundException("User with email " + email + " not found");
        }
        return orderRepository.findByUsers_Email(email);
    }

    public void delete(Long id) {
        orderRepository.delete(findById(id));
    }

    public Order update(Long id, OrderCreateDto orderCreateDto) {
        Order order = findById(id);
        orderMapper.mapOrder(orderCreateDto, order);
        return orderRepository.save(order);
    }
}
