package com.usermanagement.controller;

import com.usermanagement.dto.CreateUserDto;
import com.usermanagement.dto.UserDto;
import com.usermanagement.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserDto createUserDto) {
        log.info("inside createUser");
        UserDto userDto = userService.createUser(createUserDto, createUserDto.getCreatedBy());
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/assign-roles")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserDto> assignRoles(@PathVariable Long id, @RequestBody Set<String> roles) {
        UserDto userDto = userService.assignRoles(id, roles);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }


    @PostMapping("/{id}/approve/ApprovedBy/{ApprovedBy}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserDto> approveUser(@PathVariable Long id, @PathVariable("ApprovedBy") String approvedBy) {
        UserDto userDto = userService.approveUser(id, approvedBy);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody CreateUserDto updateUserDto) throws AccessDeniedException {
        UserDto userDto = userService.updateUser(id, updateUserDto, updateUserDto.getUpdatedBy());
        if (userDto != null) {
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}/removeBy/{Removed-By}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> removeUser(@PathVariable Long id, @PathVariable("Removed-By") String removedBy) {
        userService.removeUser(id, removedBy);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/getAllUsers")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<UserDto>> listUsers() {
        List<UserDto> users = userService.listUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{Username}/profile")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserDto> viewProfile(@PathVariable("Username") String username) {
        UserDto userDto = userService.getUserProfile(username);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}
