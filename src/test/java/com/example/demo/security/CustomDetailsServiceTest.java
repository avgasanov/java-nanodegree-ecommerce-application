package com.example.demo.security;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CustomDetailsServiceTest {

    private UserRepository userRepository = mock(UserRepository.class);
    private CustomDetailsService customDetailsService = new CustomDetailsService();

    @Before
    public void setUp() {
        TestUtils.injectObjects(customDetailsService, "userRepository", userRepository);
    }

    @Test
    public void testLoadUserByUsername() {
        User user = TestUtils.getUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        org.springframework.security.core.userdetails.User customUser =
                customDetailsService.loadUserByUsername(user.getUsername());
        assertNotNull(customUser);
        assertEquals(customUser.getUsername(), user.getUsername());
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testLoadByBadUsername() {
        when(userRepository.findByUsername(anyString()))
                .thenThrow(new UsernameNotFoundException("message"));
        customDetailsService.loadUserByUsername("username");
    }

}
