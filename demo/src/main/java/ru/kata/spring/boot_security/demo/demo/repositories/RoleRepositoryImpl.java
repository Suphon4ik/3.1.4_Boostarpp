package ru.kata.spring.boot_security.demo.demo.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.demo.model.Role;

import java.util.List;
import java.util.Optional;

@Repository
public class RoleRepositoryImpl implements RoleRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Role> findByName(String roleName) {
        TypedQuery<Role> query = entityManager.createQuery(
                "select r FROM Role r WHERE r.name = :roleName", Role.class
        );
        query.setParameter("roleName", roleName);
        List<Role> roles = query.getResultList();
        if (roles.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(roles.get(0));
        }
    }

    @Override
    public Role save(Role role) {
        if (role.getId() == null) {
            entityManager.persist(role);
            return role;
        } else {
            return entityManager.merge(role);
        }
    }

    @Override
    public List<Role> getAllRoles() {
        TypedQuery<Role> query = entityManager.createQuery(
                "SELECT r FROM Role r", Role.class
        );
        return query.getResultList();
    }

    @Override
    public List<Role> findRolesByIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }

        TypedQuery<Role> query = entityManager.createQuery(
                "SELECT r FROM Role r WHERE r.id IN :roleIds", Role.class
        );
        query.setParameter("roleIds", roleIds);
        return query.getResultList();
    }
}
