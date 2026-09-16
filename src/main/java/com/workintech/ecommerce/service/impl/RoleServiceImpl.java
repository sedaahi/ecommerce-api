package com.workintech.ecommerce.service.impl;

import com.workintech.ecommerce.entity.Role;
import com.workintech.ecommerce.repository.RoleRepository;
import com.workintech.ecommerce.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}