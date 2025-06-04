package ru.kata.spring.boot_security.demo.demo.configs;

import jakarta.transaction.Transactional;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.spring.boot_security.demo.demo.model.Role;
import ru.kata.spring.boot_security.demo.demo.model.User;
import ru.kata.spring.boot_security.demo.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.demo.repositories.UserRepository;
import ru.kata.spring.boot_security.demo.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.demo.service.UserService;

import java.util.Set;
@Configuration
public class DataInitializer {

    private final UserService userService;
    private final RoleService roleService;

    public DataInitializer(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Bean
    public ApplicationRunner initData(UserRepository userRepository
            , RoleRepository roleRepository
            , PasswordEncoder passwordEncoder) {
        return args -> initializeData(userRepository, roleRepository, passwordEncoder);
    }

    @Transactional
    protected void initializeData(UserRepository userRepository
            , RoleRepository roleRepository
            , PasswordEncoder passwordEncoder) {

        Role userRole = roleRepository
                .findByName("ROLE_USER")
                .orElseGet(() -> roleRepository
                        .save(new Role("ROLE_USER")));
        Role adminRole = roleRepository
                .findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository
                        .save(new Role("ROLE_ADMIN")));

        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(adminRole, userRole));
            admin.setCar("Lada Vesta");
            admin.setCountry("United States");
            userRepository.saveUser(admin);
        }
        if (userRepository.findByUsername("user").isEmpty()) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRoles(Set.of(userRole));
            user.setCar("Lada Granta");
            user.setCountry("Germany");
            userRepository.saveUser(user);
        }
    }
}