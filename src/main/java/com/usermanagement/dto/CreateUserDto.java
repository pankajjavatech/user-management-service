package com.usermanagement.dto;

import lombok.Data;

import java.util.Set;

@Data
public class CreateUserDto {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Set<String> roles;
    private String createdBy;
    private String updatedBy;
}

