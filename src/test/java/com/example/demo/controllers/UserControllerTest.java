package com.example.demo.controllers;

import com.example.demo.BCryptPasswordEncoder;
import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "passwordEncoder", encoder);
    }

    @Test
    public void createUser() throws Exception{
        when(encoder.encode("password123")).thenReturn("thisIsHashed");
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        final ResponseEntity<User> response = userController.createUser(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals("test", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }

    @Test
    public void createUserBadRequest() throws Exception{
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("password123");
        request.setConfirmPassword("password");

        final ResponseEntity<User> response = userController.createUser(request);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void findById() throws Exception{
        Long id = 1L;
        String username = "username";
        User user = new User();
        user.setUsername(username);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        final ResponseEntity<User> response = userController.findById(id);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(username, user.getUsername());
    }

    @Test
    public void findByBadId() throws Exception{
        Long id = 1L;
        String username = "username";
        User user = new User();
        user.setUsername(username);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        final ResponseEntity<User> response = userController.findById(id);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertEquals(username, user.getUsername());
    }

    @Test
    public void findByUsername() throws Exception{
        Long id = 1L;
        String username = "username";
        User user = new User();
        user.setUsername(username);
        user.setId(id);
        when(userRepository.findByUsername(username)).thenReturn(user);

        final ResponseEntity<User> response = userController.findByUserName(username);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals((long)id, user.getId());
    }

    @Test
    public void findByUsernameBad() throws Exception{
        Long id = 1L;
        String username = "username";
        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(user);

        final ResponseEntity<User> response = userController.findByUserName("wrongname");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

}
