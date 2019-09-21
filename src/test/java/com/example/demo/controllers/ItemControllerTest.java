package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemRepository itemRepository = mock(ItemRepository.class);
    private ItemController itemController= new ItemController();

    @Before
    public void setUp() {
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void getItemsTest() {
        Item item = getItem();

        List<Item> items = new ArrayList<>();
        items.add(item);

        when(itemRepository.findAll()).thenReturn(items);

        ResponseEntity<List<Item>> responseEntity = itemController.getItems();
        assertNotNull(responseEntity.getBody());
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertNotNull(responseEntity.getBody());
        List<Item> responseList = responseEntity.getBody();
        assertEquals(1, responseList.size());
        Item responseItem = responseList.get(0);
        assertEquals(item.getDescription(), responseItem.getDescription());
        assertEquals(item.getId(), responseItem.getId());
        assertEquals(item.getPrice(), responseItem.getPrice());
        assertEquals(item.getName(), responseItem.getName());
        assertEquals(item.hashCode(), responseItem.hashCode());
    }


    @Test
    public void getItemByIdTest() {
        Item item = getItem();
        Long id = 1L;
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        ResponseEntity<Item> responseEntity = itemController.getItemById(id);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertNotNull(responseEntity.getBody());
        Item responseItem = responseEntity.getBody();
        assertEquals(item, responseItem);
    }

    @Test
    public void getItemByBadIdTest() {
        Item item = getItem();
        Long id = 1L;
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        ResponseEntity<Item> responseEntity = itemController.getItemById(2L);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetItemsByName() {
        Item item = getItem();
        String name = item.getName();
        List<Item> items = new ArrayList<>();
        items.add(item);
        when(itemRepository.findByName(name)).thenReturn(items);

        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName(name);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        List<Item> responseList = responseEntity.getBody();
        assertEquals(1, responseList.size());
        assertEquals(item, responseList.get(0));
    }

    @Test
    public void testGetItemsByBadName() {
        Item item = getItem();
        String name = item.getName();
        List<Item> items = new ArrayList<>();
        items.add(item);
        when(itemRepository.findByName(name)).thenReturn(items);

        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName("wrong-name");
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    private Item getItem() {
        Item item = new Item();
        item.setDescription("some-description");
        item.setId(1L);
        item.setName("some-name");
        item.setPrice(BigDecimal.TEN);
        return item;
    }
}
