package fatecipi.progweb.mymanga.configs;

import fatecipi.progweb.mymanga.models.Role;
import fatecipi.progweb.mymanga.repositories.RoleRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Configuration
public class AdminUserConfig implements CommandLineRunner {
    private final RoleRepository roleRepository;

    public AdminUserConfig(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        findOrCreateRole(Role.Values.ADMIN);
        findOrCreateRole(Role.Values.BASIC);
        findOrCreateRole(Role.Values.SUBSCRIBER);
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
