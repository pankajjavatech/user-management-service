package com.usermanagement.controller;

import com.usermanagement.dto.CreateUserDto;
import com.usermanagement.dto.UserDto;
import com.usermanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_ShouldReturnCreatedUser() {
        // Arrange
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setCreatedBy("admin");
        UserDto userDto = new UserDto();
        when(userService.createUser(any(CreateUserDto.class), anyString())).thenReturn(userDto);

        // Act
        ResponseEntity<UserDto> response = userController.createUser(createUserDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userDto, response.getBody());
        verify(userService, times(1)).createUser(any(CreateUserDto.class), anyString());
    }

    @Test
    void assignRoles_ShouldReturnUserWithAssignedRoles() {
        // Arrange
        Long userId = 1L;
        Set<String> roles = new HashSet<>(Arrays.asList("ROLE_USER", "ROLE_ADMIN"));
        UserDto userDto = new UserDto();
        when(userService.assignRoles(anyLong(), anySet())).thenReturn(userDto);

        // Act
        ResponseEntity<UserDto> response = userController.assignRoles(userId, roles);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDto, response.getBody());
        verify(userService, times(1)).assignRoles(anyLong(), anySet());
    }

    @Test
    void approveUser_ShouldReturnApprovedUser() {
        // Arrange
        Long userId = 1L;
        String approvedBy = "admin";
        UserDto userDto = new UserDto();
        when(userService.approveUser(anyLong(), anyString())).thenReturn(userDto);

        // Act
        ResponseEntity<UserDto> response = userController.approveUser(userId, approvedBy);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDto, response.getBody());
        verify(userService, times(1)).approveUser(anyLong(), anyString());
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws AccessDeniedException {
        // Arrange
        Long userId = 1L;
        CreateUserDto updateUserDto = new CreateUserDto();
        updateUserDto.setUpdatedBy("admin");
        UserDto userDto = new UserDto();
        when(userService.updateUser(anyLong(), any(CreateUserDto.class), anyString())).thenReturn(userDto);

        // Act
        ResponseEntity<UserDto> response = userController.updateUser(userId, updateUserDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDto, response.getBody());
        verify(userService, times(1)).updateUser(anyLong(), any(CreateUserDto.class), anyString());
    }

    @Test
    void updateUser_ShouldReturnNotFoundWhenUserDoesNotExist() throws AccessDeniedException {
        // Arrange
        Long userId = 1L;
        CreateUserDto updateUserDto = new CreateUserDto();
        updateUserDto.setUpdatedBy("admin");
        when(userService.updateUser(anyLong(), any(CreateUserDto.class), anyString())).thenReturn(null);

        // Act
        ResponseEntity<UserDto> response = userController.updateUser(userId, updateUserDto);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService, times(1)).updateUser(anyLong(), any(CreateUserDto.class), anyString());
    }

    @Test
    void removeUser_ShouldReturnNoContent() {
        // Arrange
        Long userId = 1L;
        String removedBy = "admin";
        doNothing().when(userService).removeUser(anyLong(), anyString());

        // Act
        ResponseEntity<Void> response = userController.removeUser(userId, removedBy);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).removeUser(anyLong(), anyString());
    }

    @Test
    void listUsers_ShouldReturnListOfUsers() {
        // Arrange
        List<UserDto> users = Arrays.asList(new UserDto(), new UserDto());
        when(userService.listUsers()).thenReturn(users);

        // Act
        ResponseEntity<List<UserDto>> response = userController.listUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
        verify(userService, times(1)).listUsers();
    }

    @Test
    void viewProfile_ShouldReturnUserProfile() {
        // Arrange
        String username = "testuser";
        UserDto userDto = new UserDto();
        when(userService.getUserProfile(anyString())).thenReturn(userDto);

        // Act
        ResponseEntity<UserDto> response = userController.viewProfile(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDto, response.getBody());
        verify(userService, times(1)).getUserProfile(anyString());
    }
}
