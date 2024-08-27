package com.usermanagement.service.impl;

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
import com.usermanagement.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Override
    public UserDto createUser(CreateUserDto createUserDto, String createdBy) {
        if (createUserDto.getUsername() == null || createUserDto.getUsername().isEmpty()) {
            throw new ValidationException("Username cannot be empty");
        }

        if (createUserDto.getCreatedBy() == null) {
            throw new ValidationException("CreatedBy cannot be null");
        }

        User user = new User();
        user.setUsername(createUserDto.getUsername());
        user.setFirstName(createUserDto.getFirstName());
        user.setLastName(createUserDto.getLastName());
        user.setEmail(createUserDto.getEmail());
        user.setCreatedBy(createUserDto.getCreatedBy());

        log.info("CreateUser user:{}", user);

        Set<Role> roles = new HashSet<>();
        for (String roleName : createUserDto.getRoles()) {
            Role role = roleRepository.findByName(roleName);
            log.info("CreateUser role:{}", role);
            if (role == null) {
                throw new ResourceNotFoundException("Role not found: " + roleName);
            }
            roles.add(role);
        }
        user.setRoles(roles);

        log.info("CreateUser before calling save");

        userRepository.save(user);

        logAction("CREATE_USER", user);

        log.info("User detail {} created by {}", user, user.getCreatedBy());

        return toDto(user);
    }

    @Override
    public UserDto assignRoles(Long userId, Set<String> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName);
            if (role == null) {
                throw new ResourceNotFoundException("Role not found: " + roleName);
            }
            roles.add(role);
        }

        user.getRoles().addAll(roles);
        userRepository.save(user);

        logAction("ASSIGN_ROLES", user);

        return toDto(user);
    }


    @Override
    public UserDto approveUser(Long userId, String approvedBy) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        User approver = userRepository.findByUsername(approvedBy);
        if (approver == null) {
            throw new ResourceNotFoundException("Approver not found: " + approvedBy);
        }

        if (user.getApproved()) {
            throw new ValidationException("User is already approved.");
        }

        user.setApproved(true);
        logAction("APPROVE_USER",  user);

        return toDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, CreateUserDto updateUserDto, String updatedBy) throws AccessDeniedException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        if (!user.getCreatedBy().equals(updatedBy) || user.getApproved()) {
            throw new AccessDeniedException("Only the creator can update the user before approval.");
        }

        user.setFirstName(updateUserDto.getFirstName());
        user.setLastName(updateUserDto.getLastName());
        user.setEmail(updateUserDto.getEmail());

        logAction("UPDATE_USER", user);

        return toDto(user);
    }

    @Override
    public void removeUser(Long userId, String removedBy) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        User remover = userRepository.findByUsername(removedBy);
        if (remover == null) {
            throw new ResourceNotFoundException("Remover not found: " + removedBy);
        }

        userRepository.delete(user);
        logAction("REMOVE_USER",user);
    }

    @Override
    public List<UserDto> listUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserProfile(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User not found: " + username);
        }
        return toDto(user);
    }

    private void logAction(String action, User user) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setPerformedBy(user.getUsername());
        log.setUser(user);
        log.setDetails("User: " + user);
        auditLogRepository.save(log);
    }

    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        dto.setApproved(user.getApproved());
        return dto;
    }
}
