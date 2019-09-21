package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class CartControllerTest {

    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private CartController cartController = new CartController();

    @Before
    public void setUp() {
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }
//
//    @PostMapping("/addToCart")
//    public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
//        User user = userRepository.findByUsername(request.getUsername());
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//        Optional<Item> item = itemRepository.findById(request.getItemId());
//        if (!item.isPresent()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//        Cart cart = user.getCart();
//        IntStream.range(0, request.getQuantity())
//                .forEach(i -> cart.addItem(item.get()));
//        cartRepository.save(cart);
//        return ResponseEntity.ok(cart);
//    }

    @Test
    public void addTocartTest() {
        User user = TestUtils.getUser();
        user.getCart().setItems(new ArrayList<>());
        Item item = TestUtils.getItem();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ResponseEntity<Cart> responseEntity = cartController.addTocart(getModifyCartRequest());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        Cart cart = responseEntity.getBody();
        Mockito.verify(cartRepository, times(1)).save(cart);
        assertEquals(cart.getItems().get(0), TestUtils.getItem());
    }

    @Test
    public void addToCartBadItemIdTest() {
        User user = TestUtils.getUser();
        user.getCart().setItems(new ArrayList<>());
        Item item = TestUtils.getItem();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemRepository.findById(2L)).thenReturn(Optional.empty());
        ModifyCartRequest modifyCartRequest = getModifyCartRequest();
        modifyCartRequest.setItemId(2L);
        ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void addToCartBadUserIdTest() {
        User user = TestUtils.getUser();
        user.getCart().setItems(new ArrayList<>());
        Item item = TestUtils.getItem();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        ModifyCartRequest modifyCartRequest = getModifyCartRequest();
        modifyCartRequest.setUsername("wrong-name");
        ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void removeFromCartTest() {
        User user = TestUtils.getUser();
        Item item = user.getCart().getItems().get(0);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ModifyCartRequest modifyCartRequest = getModifyCartRequest();
        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        Cart cart = responseEntity.getBody();
        assertEquals(0, cart.getItems().size());
    }

    @Test
    public void removeFromCartBadUserTest() {
        User user = TestUtils.getUser();
        Item item = user.getCart().getItems().get(0);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(userRepository.findByUsername("wrong-name")).thenReturn(null);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ModifyCartRequest modifyCartRequest = getModifyCartRequest();
        modifyCartRequest.setUsername("wrong-name");
        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void removeFromCartBadItemTest() {
        User user = TestUtils.getUser();
        Item item = user.getCart().getItems().get(0);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemRepository.findById(2L)).thenReturn(Optional.empty());

        ModifyCartRequest modifyCartRequest = getModifyCartRequest();
        modifyCartRequest.setItemId(2L);
        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    private ModifyCartRequest getModifyCartRequest() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(TestUtils.getItem().getId());
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername(TestUtils.getUser().getUsername());
        return modifyCartRequest;
    }

}
