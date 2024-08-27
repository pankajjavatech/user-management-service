package com.usermanagement.controller;

import com.usermanagement.dto.CreateRoleDto;
import com.usermanagement.dto.RoleDto;
import com.usermanagement.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@Slf4j
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDto> createRole(@RequestBody CreateRoleDto createRoleDto) {
        log.info("Creating role with name: {}", createRoleDto.getName());
        RoleDto roleDto = roleService.createRole(createRoleDto);
        return new ResponseEntity<>(roleDto, HttpStatus.CREATED);
    }

    @PostMapping("/create-multiple")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoleDto>> createRoles(@RequestBody List<CreateRoleDto> createRoleDtos) {
        log.info("Creating multiple roles");
        List<RoleDto> roleDtos = roleService.createRoles(createRoleDtos);
        return new ResponseEntity<>(roleDtos, HttpStatus.CREATED);
    }
}
