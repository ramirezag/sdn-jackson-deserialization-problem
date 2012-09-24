package org.sample.service;

import org.sample.domain.Role;

import java.util.Set;

public interface RoleService {
    Set<Role> inflateRoles(Set<Role> roles);
}
