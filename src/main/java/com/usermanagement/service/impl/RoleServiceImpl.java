package com.usermanagement.service.impl;

import com.usermanagement.dto.CreateRoleDto;
import com.usermanagement.dto.RoleDto;
import com.usermanagement.model.Role;
import com.usermanagement.repository.RoleRepository;
import com.usermanagement.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public RoleDto createRole(CreateRoleDto createRoleDto) {
        Role role = new Role();
        role.setName(createRoleDto.getName());

        roleRepository.save(role);

        log.info("Role {} created", role.getName());

        return toDto(role);
    }

    @Override
    public List<RoleDto> createRoles(List<CreateRoleDto> createRoleDtos) {
        List<Role> roles = createRoleDtos.stream()
                .map(dto -> {
                    Role role = new Role();
                    role.setName(dto.getName());
                    return role;
                })
                .collect(Collectors.toList());

        List<Role> savedRoles = roleRepository.saveAll(roles);

        log.info("Created {} roles", savedRoles.size());

        return savedRoles.stream().map(this::toDto).collect(Collectors.toList());
    }

    private RoleDto toDto(Role role) {
        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setName(role.getName());
        return dto;
    }
}
