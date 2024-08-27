package com.usermanagement.service;

import com.usermanagement.dto.CreateUserDto;
import com.usermanagement.dto.UserDto;
import com.usermanagement.exception.ResourceNotFoundException;
import com.usermanagement.exception.ValidationException;
import com.usermanagement.model.AuditLog;
import com.usermanagement.model.Role;
import com.usermanagement.model.User;
import com.usermanagement.repository.AuditLogRepository;
import com.usermanagement.repository.RoleRepository;
import com.usermanagement.repository.UserRepository;
import com.usermanagement.service.impl.UserServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.file.AccessDeniedException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_ShouldReturnCreatedUser() {

        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setUsername("testuser");
        createUserDto.setCreatedBy("admin");
        createUserDto.setRoles(Set.of("ROLE_USER"));

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setCreatedBy("admin");

        Role role = new Role();
        role.setName("ROLE_USER");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(role);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(createUserDto, "admin");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(roleRepository, times(1)).findByName("ROLE_USER");
        verify(userRepository, times(1)).save(any(User.class));
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void createUser_ShouldThrowValidationException_WhenUsernameIsEmpty() {

        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setUsername("");
        createUserDto.setCreatedBy("admin");

        ValidationException exception = assertThrows(ValidationException.class, () -> userService.createUser(createUserDto, "admin"));
        assertEquals("Username cannot be empty", exception.getMessage());
    }

    @Test
    void createUser_ShouldThrowResourceNotFoundException_WhenRoleNotFound() {
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setUsername("testuser");
        createUserDto.setCreatedBy("admin");
        createUserDto.setRoles(Set.of("ROLE_USER"));

        when(roleRepository.findByName("ROLE_USER")).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userService.createUser(createUserDto, "admin"));
        assertEquals("Role not found: ROLE_USER", exception.getMessage());
    }

    @Test
    void assignRoles_ShouldReturnUserWithAssignedRoles() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        Role role = new Role();
        role.setName("ROLE_ADMIN");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(role);
        when(userRepository.save(any(User.class))).thenReturn(user);


        UserDto result = userService.assignRoles(userId, Set.of("ROLE_ADMIN"));

        assertNotNull(result);
        assertTrue(result.getRoles().contains("ROLE_ADMIN"));
        verify(userRepository, times(1)).findById(userId);
        verify(roleRepository, times(1)).findByName("ROLE_ADMIN");
        verify(userRepository, times(1)).save(any(User.class));
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void approveUser_ShouldReturnApprovedUser() {
        Long userId = 1L;
        String approvedBy = "admin";

        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setApproved(false);

        User approver = new User();
        approver.setUsername(approvedBy);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(approvedBy)).thenReturn(approver);

        UserDto result = userService.approveUser(userId, approvedBy);

        assertNotNull(result);
        assertTrue(result.getApproved());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findByUsername(approvedBy);
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void approveUser_ShouldThrowResourceNotFoundException_WhenUserNotFound() {
        Long userId = 1L;
        String approvedBy = "admin";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userService.approveUser(userId, approvedBy));
        assertEquals("User not found: " + userId, exception.getMessage());
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws AccessDeniedException {
        Long userId = 1L;
        CreateUserDto updateUserDto = new CreateUserDto();
        updateUserDto.setFirstName("UpdatedFirstName");
        updateUserDto.setLastName("UpdatedLastName");
        updateUserDto.setEmail("updated@example.com");
        updateUserDto.setUpdatedBy("admin");

        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setCreatedBy("admin");
        user.setApproved(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.updateUser(userId, updateUserDto, "admin");

        assertNotNull(result);
        assertEquals("UpdatedFirstName", result.getFirstName());
        assertEquals("UpdatedLastName", result.getLastName());
        assertEquals("updated@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void updateUser_ShouldThrowAccessDeniedException_WhenUserIsNotCreatorOrApproved() {
        Long userId = 1L;
        CreateUserDto updateUserDto = new CreateUserDto();
        updateUserDto.setUpdatedBy("anotherUser");

        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setCreatedBy("admin");
        user.setApproved(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> userService.updateUser(userId, updateUserDto, "anotherUser"));
        assertEquals("Only the creator can update the user before approval.", exception.getMessage());
    }

    @Test
    void removeUser_ShouldRemoveUserSuccessfully() {
        Long userId = 1L;
        String removedBy = "admin";

        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        User remover = new User();
        remover.setUsername(removedBy);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(removedBy)).thenReturn(remover);

        userService.removeUser(userId, removedBy);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(user);
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void removeUser_ShouldThrowResourceNotFoundException_WhenUserNotFound() {
        Long userId = 1L;
        String removedBy = "admin";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userService.removeUser(userId, removedBy));
        assertEquals("User not found: " + userId, exception.getMessage());
    }

    @Test
    void listUsers_ShouldReturnListOfUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<UserDto> result = userService.listUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserProfile_ShouldReturnUserProfile() {
        String username = "testuser";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(user);

        UserDto result = userService.getUserProfile(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void getUserProfile_ShouldThrowResourceNotFoundException_WhenUserNotFound() {
        String username = "unknownUser";

        when(userRepository.findByUsername(username)).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userService.getUserProfile(username));
        assertEquals("User not found: " + username, exception.getMessage());
    }
}
