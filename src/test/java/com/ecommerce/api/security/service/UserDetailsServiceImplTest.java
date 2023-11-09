package com.ecommerce.api.security.service;

import com.ecommerce.api.SampleData;
import com.ecommerce.api.TestUtils;
import com.ecommerce.api.model.persistence.User;
import com.ecommerce.api.model.persistence.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test without using @Mock annotation
 */
public class UserDetailsServiceImplTest {

    private UserRepository userRepo = mock(UserRepository.class);

    private UserDetailsServiceImpl userDetailsService;

    private static User sampleUser;

    @BeforeAll
    public static void beforeAll() {
        sampleUser = SampleData.getSampleUser();
    }

    @BeforeEach
    public void beforeEach() {
        userDetailsService = new UserDetailsServiceImpl();
        TestUtils.injectObject("userRepository", userRepo, userDetailsService);
    }


    @Test
    public void test_loadUserByUsername() {
        when(userRepo.findByUsername(anyString())).thenReturn(sampleUser);

        UserDetails userDetails = userDetailsService.loadUserByUsername(sampleUser.getUsername());
        Assertions.assertNotNull(userDetails);
    }

    @Test
    public void test_loadUserByUsername_UsernameNotFoundException() {
        when(userRepo.findByUsername(anyString())).thenReturn(null);

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(sampleUser.getUsername());
        });

    }

}
