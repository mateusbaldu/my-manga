package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.Order;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.models.dtos.OrderDto;
import fatecipi.progweb.mymanga.models.mappers.OrderMapper;
import fatecipi.progweb.mymanga.repositories.MangaVolumeRepository;
import fatecipi.progweb.mymanga.repositories.OrderRepository;
import fatecipi.progweb.mymanga.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private MangaVolumeRepository mangaVolumeRepository;

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

    public Order update(Long id, OrderDto orderDto) {
        Order order = findById(id);
        orderMapper.mapOrder(orderDto, order);
        return orderRepository.save(order);
    }

    @Transactional
    public Order createOrder(OrderDto dto) {
        Users user = userRepository
                .findById(dto.users().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User " + dto.users().getName() + " not found"));

        Order newOrder = new Order();
        orderMapper.mapOrder(dto, newOrder);

        // 3. Itera sobre os itens do DTO para criar as entidades "filhas"
        for (OrderItemDTO itemDto : order.getItems()) {
            // Encontra o produto que está sendo comprado
            MangaVolume volume = volumeRepository.findById(itemDto.getVolumeId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Volume não encontrado"));

            // Lógica de negócio: verificar estoque, etc.
            if (volume.getStockQuantity() < itemDto.getQuantity()) {
                throw new RegraDeNegocioException("Estoque insuficiente para o volume " + volume.getId());
            }

            // Cria a entidade "filha" (o ItemPedido)
            OrderItem novoItem = new OrderItem();
            novoItem.setMangaVolume(volume);
            novoItem.setQuantity(itemDto.getQuantity());
            novoItem.setPrice(volume.getPrice()); // Salva o preço no momento da compra

            // A MÁGICA: Adiciona o filho ao pai (bidirecional)
            newOrder.addItem(novoItem); // (Um método helper que faz novoItem.setOrder(this) e this.items.add(novoItem))
        }

        // 4. Lógica de negócio: calcular o valor final, etc.
        newOrder.calculateFinalPrice();

        // 5. Salva a entidade "mãe". Graças ao CascadeType.ALL, os "filhos" (OrderItems) serão salvos automaticamente.
        return orderRepository.save(newOrder);
    }
}
