package fatecipi.progweb.mymanga.models.user;

import fatecipi.progweb.mymanga.configs.generator.UuidV7Generator;
import fatecipi.progweb.mymanga.dto.security.LoginRequestDto;
import fatecipi.progweb.mymanga.models.order.Order;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {
    @Id
    @GeneratedValue(generator = "uuidv7")
    @GenericGenerator(name = "uuidv7", type = UuidV7Generator.class)
    @Column(name = "user_id")
    private UUID id;

    @Column(unique = true)
    private String email;
    private String name;
    private String password;
    private Instant createdAt;

    @Embedded
    private Adress adress;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "tb_users_roles",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    public boolean isLoginCorrect(LoginRequestDto loginRequestDto, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(loginRequestDto.password(), this.password);
    }
}
