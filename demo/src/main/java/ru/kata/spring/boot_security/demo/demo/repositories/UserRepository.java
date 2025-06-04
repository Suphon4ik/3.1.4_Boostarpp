package ru.kata.spring.boot_security.demo.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kata.spring.boot_security.demo.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> getUserById(Long id);

    Optional<User> findByUsername(String name);

    List<User> getAllUsers();

    void saveUser(User user);

    void deleteUser(Long id);

    void updateUser(Long id, User user,List<Long> roleIds);
}
