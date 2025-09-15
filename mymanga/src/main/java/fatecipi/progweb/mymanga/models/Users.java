package fatecipi.progweb.mymanga.models;

import fatecipi.progweb.mymanga.configs.generator.GeneratedUuidV7;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {
    @Id
    @GeneratedUuidV7
    @Column(name = "user_id")
    private UUID id;

    @Email
    private String email;
    private String name;
    private String password;
    private Instant createdAt;

    @Embedded
    private Adress adress;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();
}
