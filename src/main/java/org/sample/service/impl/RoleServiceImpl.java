package org.sample.service.impl;

import org.sample.domain.Role;
import org.sample.repository.RoleRepository;
import org.sample.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Set<Role> inflateRoles(Set<Role> roles) {
        if (roles.size() > 0) {
            for (Role role : roles) {
                assert role != null;
                final Role exists = roleRepository.findByUid(role.getUid());
                if (null != exists) {
                    role.setName(exists.getName());
                    role.setId(exists.getId());
                } else {
                    throw new IllegalArgumentException("Invalid role specified: " + role.getUid());
                }
            }
        }
        return roles;
    }
}
