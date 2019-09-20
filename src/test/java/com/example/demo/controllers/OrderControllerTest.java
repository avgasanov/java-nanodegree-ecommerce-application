package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void submitTest() throws Exception{
        String username = "some-user";
        User user = new User();
        user.setId(1);
        user.setUsername(username);
        user.setCart(new Cart(1L, new ArrayList<>(), user, BigDecimal.TEN));
        when(userRepository.findByUsername(username)).thenReturn(user);
        final ResponseEntity<UserOrder> response = orderController.submit(username);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        UserOrder userOrder = response.getBody();
        assertNotNull(userOrder);
        assertEquals(BigDecimal.TEN, userOrder.getTotal());
        assertEquals(new ArrayList<>(), userOrder.getItems());
    }


    @Test
    public void submitTestBad() throws Exception{
        String username = "some-user";
        User user = new User();
        user.setId(1);
        user.setUsername(username);
        user.setCart(new Cart(1L, new ArrayList<>(), user, BigDecimal.TEN));
        when(userRepository.findByUsername(username)).thenReturn(user);
        final ResponseEntity<UserOrder> response = orderController.submit("wrong-name");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getOrdersForUserTest() throws Exception{
        String username = "some-user";
        User user = new User();
        user.setId(1);
        user.setUsername(username);
        user.setCart(new Cart(1L, new ArrayList<>(), user, BigDecimal.TEN));
        when(userRepository.findByUsername(username)).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(new ArrayList<>());
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(username);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(new ArrayList<UserOrder>(), response.getBody());
    }

    @Test
    public void getOrdersForBadUserTest() throws Exception{
        String username = "some-user";
        User user = new User();
        user.setId(1);
        user.setUsername(username);
        user.setCart(new Cart(1L, new ArrayList<>(), user, BigDecimal.TEN));
        when(userRepository.findByUsername(username)).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(new ArrayList<>());
        final ResponseEntity<List<UserOrder>> response =
                orderController.getOrdersForUser("wrong-name");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

}
