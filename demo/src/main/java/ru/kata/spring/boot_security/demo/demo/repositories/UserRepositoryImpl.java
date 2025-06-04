package ru.kata.spring.boot_security.demo.demo.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.demo.model.Role;
import ru.kata.spring.boot_security.demo.demo.model.User;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    @Override
    public Optional<User> findByUsername(String name) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.username = :username", User.class
        );
        query.setParameter("username", name);
        List<User> users = query.getResultList();
        return users.isEmpty() ? Optional.empty() : Optional.ofNullable(users.get(0));
    }

    @Override
    public List<User> getAllUsers() {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u", User.class
        );
        return query.getResultList();
    }

    @Override
    public void saveUser(User user) {
        if (user.getId() == null) {
            entityManager.persist(user);
        } else {
            entityManager.merge(user);
        }
    }

    @Override
    public void deleteUser(Long id) {
        User user = entityManager.find(User.class, id);
        if (user != null) {
            entityManager.remove(user);
        }
    }

    @Override
    public void updateUser(Long id, User user, List<Long> roleIds) {
        User existingUser = entityManager.find(User.class, id);
        if (existingUser == null) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }

        existingUser.setUsername(user.getUsername());
        existingUser.setCar(user.getCar());
        existingUser.setCountry(user.getCountry());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(user.getPassword());
        }

        if (roleIds != null && !roleIds.isEmpty()) {
            TypedQuery<Role> query = entityManager.createQuery(
                    "SELECT r FROM Role r WHERE r.id IN :roleIds", Role.class
            );
            query.setParameter("roleIds", roleIds);
            List<Role> roles = query.getResultList();
            existingUser.getRoles().clear();
            existingUser.getRoles().addAll(roles);
        }

        entityManager.merge(existingUser);

    }
}
