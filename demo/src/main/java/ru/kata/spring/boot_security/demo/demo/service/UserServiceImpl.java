package ru.kata.spring.boot_security.demo.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.demo.model.Role;
import ru.kata.spring.boot_security.demo.demo.model.User;
import ru.kata.spring.boot_security.demo.demo.repositories.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Transactional
    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(userRepository.getUserById(id).orElseThrow(() -> new RuntimeException("User not found")));
    }

    @Transactional
    @Override
    public Optional<User> findByUsername(String name) {
        return Optional.ofNullable(userRepository.findByUsername(name).orElse(null));
    }

    @Transactional
    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Transactional
    @Override
    public void saveUser(User user) {
      /*   Role userRole = roleService.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Role 'ROLE_USER' not found"));
        user.getRoles().add(userRole);*/

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.saveUser(user);
        }
    }
    @Transactional
    @Override
    public void deleteUser(Long id) {
        User user = userRepository.getUserById(id)
                .orElseThrow(() -> new IllegalStateException("User with id " + id + " not found"));
        if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
            throw new IllegalStateException("Нельзя удалить администратора!");
        }
        userRepository.deleteUser(user.getId());
    }

    @Transactional
    @Override
    public void updateUser(Long id, User user, List<Long> roleIds) {
        User existingUser = userRepository.getUserById(id)
                .orElseThrow(() -> new IllegalStateException("User with id " + id + " not found"));

        boolean isAdmin = existingUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"))
                && existingUser.getUsername().equals("admin");


        if (isAdmin) {
            throw new IllegalStateException("Нельзя изменить администратора!");
        }
        existingUser.setUsername(user.getUsername());
        existingUser.setCountry(user.getCountry());
        existingUser.setCar(user.getCar());

        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        }
        if (roleIds != null && !roleIds.isEmpty()) {
            List<Role> roles = roleService.findRolesByIds(roleIds);

            if (roles.isEmpty()) {
                throw new IllegalStateException("Указанные роли не найдены!");
            }
            existingUser.setRoles(new HashSet<>(roles));
        } else {
        }
        userRepository.saveUser(existingUser);
    }
}

