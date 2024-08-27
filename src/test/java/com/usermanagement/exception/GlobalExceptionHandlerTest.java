package com.usermanagement.exception;

import com.usermanagement.controller.UserController;
import com.usermanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = UserController.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        //MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void testHandleResourceNotFoundException() throws Exception {
        when(userService.getUserProfile("nonexistent")).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/users/profile")
                        .header("Username", "nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{'message': 'User not found', 'status': 404}"));
    }

    @Test
    public void testHandleAccessDeniedException() throws Exception {
        when(userService.getUserProfile("admin")).thenThrow(new AccessDeniedException("Access denied"));

        mockMvc.perform(get("/api/users/profile")
                        .header("Username", "admin"))
                .andExpect(status().isForbidden())
                .andExpect(content().json("{'message': 'Access denied', 'status': 403}"));
    }
}

