package com;

import com.controllers.OrderController;
import com.model.persistence.Cart;
import com.model.persistence.Item;
import com.model.persistence.User;
import com.model.persistence.UserOrder;
import com.model.persistence.repositories.OrderRepository;
import com.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderTest {

    private OrderController orderController;
    private OrderRepository orderRepo = mock(OrderRepository.class);
    private UserRepository userRepo = mock(UserRepository.class);

    private static final String VALID_USERNAME = "Bassam";
    private static final String INVALID_USERNAME = "NotBassam!";

    @Before
    public void initData(){
        this.orderController = new OrderController(null, null);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
        TestUtils.injectObjects(orderController, "userRepository", userRepo);

        Item item = createItem();
        List<Item> items = new ArrayList<Item>();
        items.add(item);

        User user = createUser();
        Cart cart = createCart(user, items);
        user.setCart(cart);

        when(userRepo.findByUsername(VALID_USERNAME)).thenReturn(user);
        when(userRepo.findByUsername(INVALID_USERNAME)).thenReturn(null);
    }

    private Item createItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Lego");
        BigDecimal price = BigDecimal.valueOf(2.99);
        item.setPrice(price);
        item.setDescription("The office Lego");
        return item;
    }

    private User createUser() {
        User user = new User();
        user.setId(0);
        user.setUsername(VALID_USERNAME);
        user.setPassword("Michel is the best boos ever");
        return user;
    }

    private Cart createCart(User user, List<Item> items) {
        Cart cart = new Cart();
        cart.setId(0L);
        cart.setUser(user);
        cart.setItems(items);
        BigDecimal total = BigDecimal.valueOf(2.99);
        cart.setTotal(total);
        return cart;
    }

    @Test
    public void whenSubmitOrderWithValidUser_thenOrderIsSubmitted() {
        ResponseEntity<UserOrder> response = orderController.submit(VALID_USERNAME);
        assertSuccessfulResponse(response);
        UserOrder order = response.getBody();
        assertNotNull(order);
        assertEquals(1, order.getItems().size());
    }

    @Test
    public void whenSubmitOrderWithInvalidUser_thenReturnsNotFound() {
        ResponseEntity<UserOrder> response = orderController.submit(INVALID_USERNAME);
        assertNotFoundResponse(response);
    }

    @Test
    public void whenGetOrdersForValidUser_thenReturnsOrders() {
        ResponseEntity<List<UserOrder>> ordersForUser = orderController.getOrdersForUser(VALID_USERNAME);
        assertSuccessfulResponse(ordersForUser);
        List<UserOrder> orders = ordersForUser.getBody();
        assertNotNull(orders);
    }

    @Test
    public void whenGetOrdersForInvalidUser_thenReturnsNotFound() {
        ResponseEntity<List<UserOrder>> ordersForUser = orderController.getOrdersForUser(INVALID_USERNAME);
        assertNotFoundResponse(ordersForUser);
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