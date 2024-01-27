package com;

import com.controllers.CartController;
import com.model.persistence.Cart;
import com.model.persistence.Item;
import com.model.persistence.User;
import com.model.persistence.repositories.CartRepository;
import com.model.persistence.repositories.ItemRepository;
import com.model.persistence.repositories.UserRepository;
import com.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;


import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartTest {

    private CartController cartController;
    private UserRepository userRepoMock = mock(UserRepository.class);
    private CartRepository cartRepoMock = mock(CartRepository.class);
    private ItemRepository itemRepoMock = mock(ItemRepository.class);

    private static final String VALID_USERNAME = "Bassam";
    private static final String INVALID_USERNAME = "NotBassam!";
    private static final Long VALID_ITEM_ID = 1L;
    private static final Long INVALID_ITEM_ID = 2L;

    @Before
    public void initData() {
        this.cartController = new CartController(null, null, null);
        TestUtils.injectObjects(cartController, "userRepository", userRepoMock);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepoMock);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepoMock);

        User user = createUser();
        when(userRepoMock.findByUsername(VALID_USERNAME)).thenReturn(user);

        Item item = createItem();
        when(itemRepoMock.findById(VALID_ITEM_ID)).thenReturn(java.util.Optional.of(item));
    }

    private User createUser() {
        User user = new User();
        Cart cart = new Cart();
        user.setId(0);
        user.setUsername(VALID_USERNAME);
        user.setPassword("123123123");
        user.setCart(cart);
        return user;
    }

    private Item createItem() {
        Item item = new Item();
        item.setId(VALID_ITEM_ID);
        item.setName("Bassam");
        BigDecimal price = BigDecimal.valueOf(2.99);
        item.setPrice(price);
        item.setDescription("1111");
        return item;
    }

    @Test
    public void testAddToCartWithValidUserAndItem() {
        ModifyCartRequest request = createRequest(VALID_ITEM_ID, 1, VALID_USERNAME);
        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertSuccessfulResponse(response);
        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(BigDecimal.valueOf(2.99), cart.getTotal());
    }

    @Test
    public void testAddToCartWithInvalidUser() {
        ModifyCartRequest request = createRequest(VALID_ITEM_ID, 1, INVALID_USERNAME);
        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertNotFoundResponse(response);
    }

    @Test
    public void testAddToCartWithInvalidItem() {
        ModifyCartRequest request = createRequest(INVALID_ITEM_ID, 1, VALID_USERNAME);
        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertNotFoundResponse(response);
    }

    @Test
    public void testRemoveFromCartWithValidUserAndItem() {
        ModifyCartRequest request = createRequest(VALID_ITEM_ID, 2, VALID_USERNAME);
        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertSuccessfulResponse(response);

        request = createRequest(VALID_ITEM_ID, 1, VALID_USERNAME);
        response = cartController.removeFromcart(request);

        assertSuccessfulResponse(response);
        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(BigDecimal.valueOf(2.99), cart.getTotal());
    }

    @Test
    public void testRemoveFromCartWithInvalidUser() {
        ModifyCartRequest request = createRequest(VALID_ITEM_ID, 1, INVALID_USERNAME);
        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertNotFoundResponse(response);
    }

    @Test
    public void testRemoveFromCartWithInvalidItem() {
        ModifyCartRequest request = createRequest(INVALID_ITEM_ID, 1, VALID_USERNAME);
        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertNotFoundResponse(response);
    }

    public ModifyCartRequest createRequest(Long itemId, int quantity, String username) {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(itemId);
        request.setQuantity(quantity);
        request.setUsername(username);
        return request;
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