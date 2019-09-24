package com.example.demo.controllers;

import java.util.Optional;

import com.example.demo.BCryptPasswordEncoder;
import com.example.demo.splunk.SplunkHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private SplunkHelper splunkHelper;

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        if(userRepository.findById(id) == null || !userRepository.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.of(userRepository.findById(id));
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        String password = createUserRequest.getPassword();
        String confirmPassword = createUserRequest.getConfirmPassword();
        if( password == null ||
                !password.equals(confirmPassword) ||
                 password.isEmpty() || password.length() < 8) {
            splunkHelper.logRequestFailure("Do not meet password requirements in user creation");
            return ResponseEntity.badRequest().build();
        }
        password = passwordEncoder.encode(password);
        user.setPassword(password);
        Cart cart = new Cart();
        cartRepository.save(cart);
        user.setCart(cart);
        userRepository.save(user);
        splunkHelper.logRequestSuccess("User creation request is successful "
                + user.getUsername());
        return ResponseEntity.ok(user);
    }

}
