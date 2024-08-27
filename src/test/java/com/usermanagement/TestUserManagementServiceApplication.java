package com.usermanagement;

import org.springframework.boot.SpringApplication;

public class TestUserManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(UserManagementServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
