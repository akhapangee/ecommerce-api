package com.ecommerce.api.controllers;


import com.ecommerce.api.SampleData;
import com.ecommerce.api.exception.ApiRequestException;
import com.ecommerce.api.model.persistence.User;
import com.ecommerce.api.model.persistence.repositories.CartRepository;
import com.ecommerce.api.model.persistence.repositories.UserRepository;
import com.ecommerce.api.model.requests.CreateUserRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserController userController;

    private static User user;

    @BeforeAll
    public static void init() {
        user = SampleData.getSampleUser();
    }

    @Test
    public void test_createUser() {
        when(bCryptPasswordEncoder.encode(user.getPassword())).thenReturn("hashedTestPassword");
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(user.getUsername());
        request.setPassword(user.getPassword());
        request.setConfirmPassword(user.getPassword());

        final ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(request.getUsername(), response.getBody().getUsername());
        assertEquals("hashedTestPassword", response.getBody().getPassword());
    }

    @Test
    public void test_createUser_Already_Exists() {
        when(userRepository.findByUsername(anyString())).thenReturn(user);

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(user.getUsername());
        request.setPassword(user.getPassword());
        request.setConfirmPassword(user.getPassword());

        Assertions.assertThrows(ApiRequestException.class, () -> {
            userController.createUser(request);
        });
    }

    @Test
    public void testPasswordRequirements() {
        CreateUserRequest request = new CreateUserRequest();
        // Empty password check
        assertThrows(ApiRequestException.class, () -> {
            userController.createUser(request);
        });

        // Check password and confirm password are not same
        request.setPassword("pass1234");
        request.setConfirmPassword("pass12345");
        assertThrows(ApiRequestException.class, () -> {
            userController.createUser(request);
        });

        // Check password length
        request.setPassword("pass");
        request.setConfirmPassword("pass");
        assertThrows(ApiRequestException.class, () -> {
            userController.createUser(request);
        });

    }


    @Test
    public void testFindByUserName() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        ResponseEntity<User> response = userController.findByUserName(user.getUsername());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user.getUsername(), response.getBody().getUsername());
    }

    @Test
    public void testFindById() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        ResponseEntity<User> response = userController.findById(user.getId());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user.getId(), response.getBody().getId());
    }
}
