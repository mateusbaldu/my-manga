package fatecipi.progweb.mymanga.models.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "role")
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;
    private String name;

    public enum Values {
        BASIC(2L), SUBSCRIBER(3L), ADMIN(1L);

        Long roleId;
        Values(Long roleId) {
            this.roleId = roleId;
        }
        public Long getRoleId() {
            return roleId;
        }
    }
}