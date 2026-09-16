package com.workintech.ecommerce.controller;

import com.workintech.ecommerce.entity.Role;
import com.workintech.ecommerce.service.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // GET /roles
    @GetMapping
    public List<Role> getRoles() {
        return roleService.getAllRoles();
    }
}