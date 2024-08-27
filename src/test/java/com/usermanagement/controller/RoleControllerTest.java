package com.usermanagement.controller;

import com.usermanagement.dto.CreateRoleDto;
import com.usermanagement.dto.RoleDto;
import com.usermanagement.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createRole_ShouldReturnCreatedRole() {
        CreateRoleDto createRoleDto = new CreateRoleDto();
        createRoleDto.setName("ROLE_USER");
        RoleDto roleDto = new RoleDto();
        when(roleService.createRole(any(CreateRoleDto.class))).thenReturn(roleDto);

        ResponseEntity<RoleDto> response = roleController.createRole(createRoleDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(roleDto, response.getBody());
        verify(roleService, times(1)).createRole(any(CreateRoleDto.class));
    }

    @Test
    void createRoles_ShouldReturnCreatedRoles() {
        List<CreateRoleDto> createRoleDtos = Arrays.asList(new CreateRoleDto(), new CreateRoleDto());
        List<RoleDto> roleDtos = Arrays.asList(new RoleDto(), new RoleDto());
        when(roleService.createRoles(anyList())).thenReturn(roleDtos);

        ResponseEntity<List<RoleDto>> response = roleController.createRoles(createRoleDtos);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(roleDtos, response.getBody());
        verify(roleService, times(1)).createRoles(anyList());
    }
}
