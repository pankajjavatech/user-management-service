package com.usermanagement.service;


import com.usermanagement.dto.CreateRoleDto;
import com.usermanagement.dto.RoleDto;

import java.util.List;

public interface RoleService {
    RoleDto createRole(CreateRoleDto createRoleDto);
    List<RoleDto> createRoles(List<CreateRoleDto> createRoleDtos);  // New method
}
