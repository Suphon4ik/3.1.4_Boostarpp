package ru.kata.spring.boot_security.demo.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.demo.model.Role;
import ru.kata.spring.boot_security.demo.demo.repositories.RoleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService{

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional
    @Override
    public Optional<Role> findByName(String roleName) {
        return roleRepository.findByName(roleName);
    }

    @Transactional
    @Override
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    @Transactional
    @Override
    public List<Role> getAllRoles() {
        return roleRepository.getAllRoles();
    }

    @Transactional
    @Override
    public List<Role> findRolesByIds(List<Long> roleIds) {
        return roleRepository.findRolesByIds(roleIds);
    }

}
