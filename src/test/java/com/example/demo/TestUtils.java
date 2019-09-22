package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    public static void injectObjects(Object target, String fieldName, Object toInject) {
        boolean wasPrivate = false;

        try {
            Field f = target.getClass().getDeclaredField(fieldName);

            if(!f.isAccessible()) {
                f.setAccessible(true);
                wasPrivate = true;
            }
            f.set(target, toInject);
            if(wasPrivate) {
                f.setAccessible(false);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static User getUserWithoutCart() {
        User user = new User();
        user.setUsername("some-name");
        user.setId(1L);
        user.setPassword("password");
        return user;
    }

    public static User getUser() {
        User user = getUserWithoutCart();
        user.setCart(getCart(user));
        return user;
    }

    public static Cart getCartWithoutUser() {
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(getItems());
        cart.setTotal(BigDecimal.TEN);
        return cart;
    }

    public static Cart getCart(User user) {
        Cart cart = getCartWithoutUser();
        cart.setUser(user);
        return cart;
    }

    private static List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        items.add(getItem());
        return items;
    }

    public static Item getItem() {
        Item item = new Item();
        item.setDescription("some-description");
        item.setId(1L);
        item.setName("some-name");
        item.setPrice(BigDecimal.TEN);
        return item;
    }
}
