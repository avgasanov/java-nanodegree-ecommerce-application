package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.AuthenticationRequest;
import com.example.demo.security.JwtTokenProvider;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    private AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
    private UserRepository users = mock(UserRepository.class);
    private AuthController authController = new AuthController();

    @Before
    public void setUp() {
        TestUtils.injectObjects(
                authController, "authenticationManager", authenticationManager);
        TestUtils.injectObjects(
                authController, "jwtTokenProvider", jwtTokenProvider);
        TestUtils.injectObjects(
                authController, "users", users);
    }

    @Test
    public void signinTest() {
        AuthenticationRequest authenticationRequest = getAuthRequest();
        String token = "token";
        Map<Object, Object> model = getModel(authenticationRequest.getUsername(), token);
        when(jwtTokenProvider.createToken(
                authenticationRequest.getUsername(), new ArrayList<>())).thenReturn(token);
        ResponseEntity<Map<Object,Object>> responseEntity =
                authController.signin(authenticationRequest);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Map<Object,Object> responseModel = responseEntity.getBody();
        assertNotNull(responseModel);
        assertEquals(model, responseModel);
        verify(authenticationManager, times(1))
                .authenticate(any());
        verify(jwtTokenProvider, times(1))
                .createToken(authenticationRequest.getUsername(), new ArrayList<>());
    }

    @Test(expected = AuthenticationException.class)
    public void signinBadTest() throws AuthenticationException {
        AuthenticationRequest authenticationRequest = getAuthRequest();
        String token = "token";
        Map<Object, Object> model = getModel(authenticationRequest.getUsername(), token);
        when(jwtTokenProvider.createToken(
                authenticationRequest.getUsername(), new ArrayList<>())).thenReturn(token);
        when(authenticationManager.authenticate(anyObject()))
                .thenThrow(new AuthenticationException("message") {
            @Override
            public String getMessage() {
                return super.getMessage();
            }
        });
        ResponseEntity<Map<Object,Object>> responseEntity =
                authController.signin(authenticationRequest);
    }

    private AuthenticationRequest getAuthRequest() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setPassword("some-password");
        authenticationRequest.setUsername("some-username");
        return authenticationRequest;
    }

    private Map<Object, Object> getModel(String username, String token) {
        Map<Object, Object> model = new HashMap<>();
        model.put("username", username);
        model.put("token", token);
        return model;
    }
}
