package com;

import com.controllers.ItemController;
import com.model.persistence.Item;
import com.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemTest {

    private ItemController itemController;
    private ItemRepository itemRepoMock = mock(ItemRepository.class);
    private static final Long VALID_ITEM_ID = 1L;
    private static final Long INVALID_ITEM_ID = 2L;
    private static final String VALID_ITEM_NAME = "Bassam";
    private static final String INVALID_ITEM_NAME = "NotBassam!";

    @Before
    public void initData(){
        this.itemController = new ItemController(null);
        TestUtils.injectObjects(itemController, "itemRepository", itemRepoMock);
        Item item = createItem();
        when(itemRepoMock.findAll()).thenReturn(Collections.singletonList(item));
        when(itemRepoMock.findById(VALID_ITEM_ID)).thenReturn(java.util.Optional.of(item));
        when(itemRepoMock.findByName(VALID_ITEM_NAME)).thenReturn(Collections.singletonList(item));
    }

    private Item createItem() {
        Item item = new Item();
        item.setId(VALID_ITEM_ID);
        item.setName(VALID_ITEM_NAME);
        BigDecimal price = BigDecimal.valueOf(2.99);
        item.setPrice(price);
        item.setDescription("maybe it's item maybe it's not");
        return item;
    }

    @Test
    public void testGetAllItemsReturnsAllItems() {
        ResponseEntity<List<Item>> response = itemController.getItems();

        assertSuccessfulResponse(response);
        List<Item> items = response.getBody();
        assertNotNull(items);
        assertEquals(1, items.size());
    }

    @Test
    public void testGetItemByIdWithValidIdReturnsItem() {
        ResponseEntity<Item> response = itemController.getItemById(VALID_ITEM_ID);

        assertSuccessfulResponse(response);
        Item item = response.getBody();
        assertNotNull(item);
    }

    @Test
    public void testGetItemByIdWithInvalidIdReturnsNotFound() {
        ResponseEntity<Item> response = itemController.getItemById(INVALID_ITEM_ID);

        assertNotFoundResponse(response);
    }

    @Test
    public void testGetItemsByNameWithValidNameReturnsItems() {
        ResponseEntity<List<Item>> response = itemController.getItemsByName(VALID_ITEM_NAME);

        assertSuccessfulResponse(response);
        List<Item> items = response.getBody();
        assertNotNull(items);
        assertEquals(1, items.size());
    }

    @Test
    public void testGetItemsByNameWithInvalidNameReturnsNotFound() {
        ResponseEntity<List<Item>> response = itemController.getItemsByName(INVALID_ITEM_NAME);

        assertNotFoundResponse(response);
    }

    private void assertSuccessfulResponse(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
    }

    private void assertNotFoundResponse(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

}