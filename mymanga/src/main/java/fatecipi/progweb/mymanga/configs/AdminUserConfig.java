package fatecipi.progweb.mymanga.configs;

import fatecipi.progweb.mymanga.models.Role;
import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.repositories.RoleRepository;
import fatecipi.progweb.mymanga.repositories.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@Log4j2
@Configuration
public class AdminUserConfig implements CommandLineRunner {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public AdminUserConfig(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Role role = findOrCreateRole(Role.Values.ADMIN);
        Optional<Users> user = userRepository.findByEmail("admin@mymanga.com");
        user.ifPresentOrElse(
                users -> log.info("admin já existe"),
                () -> {
                    Users users = new Users();
                    users.setName("Admin");
                    users.setEmail("admin@mymanga.com");
                    Set<Role> set = Set.of(role);
                    users.setRoles(set);
                    users.setActive(true);
                    users.setUsername("admin");
                    users.setPassword(passwordEncoder.encode("admin123"));
                    users.setCreatedAt(Instant.now());
                    userRepository.save(users);
                });
        Role basicRole = findOrCreateRole(Role.Values.BASIC);
        findOrCreateRole(Role.Values.SUBSCRIBER);
        userRepository.findByEmail("usertest@mymanga.com").ifPresentOrElse(
                userTest -> log.info("Usuário de teste já existe"),
                () -> {
                    Users testUser = new Users();
                    testUser.setName("Teste");
                    testUser.setEmail("usertest@mymanga.com");
                    testUser.setRoles(Set.of(basicRole));
                    testUser.setActive(true);
                    testUser.setUsername("usertest");
                    testUser.setPassword(passwordEncoder.encode("teste123"));
                    testUser.setCreatedAt(Instant.now());
                    userRepository.save(testUser);
                }
        );
    }

    private Role findOrCreateRole(Role.Values roleValue) {
        Role role = roleRepository.findByName(roleValue.name());
        if (role == null) {
            role = new Role();
            role.setName(roleValue.name());
            roleRepository.saveAndFlush(role);
            log.info("Role '{}' criada com sucesso", roleValue.name());
        }
        return role;
    }
}
