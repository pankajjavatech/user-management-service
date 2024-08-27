package com.usermanagement.service;

import com.usermanagement.dto.CreateUserDto;
import com.usermanagement.dto.UserDto;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Set;

public interface UserService {

    UserDto createUser(CreateUserDto createUserDto, String createdBy);

    UserDto assignRoles(Long userId, Set<String> roleNames);

    UserDto approveUser(Long userId, String approvedBy);

    UserDto updateUser(Long userId, CreateUserDto updateUserDto, String updatedBy) throws AccessDeniedException;

    void removeUser(Long userId, String removedBy);

    List<UserDto> listUsers();

    UserDto getUserProfile(String username);

}
