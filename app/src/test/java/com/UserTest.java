package com;

import com.controllers.UserController;
import com.model.persistence.Cart;
import com.model.persistence.User;
import com.model.persistence.repositories.CartRepository;
import com.model.persistence.repositories.UserRepository;
import com.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserTest {

    private UserController userController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    private static  String VALID_USERNAME = "Bassam";
    private static  String INVALID_USERNAME = "NotBassam!";
    private static  Long VALID_USER_ID = 0L;
    private static  Long INVALID_USER_ID = 1L;

    @Before
    public void setUp() {
        userController = new UserController(null, null, null);
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);

        User user = createUser();
        when(userRepo.findByUsername(VALID_USERNAME)).thenReturn(user);
        when(userRepo.findById(VALID_USER_ID)).thenReturn(java.util.Optional.of(user));
        when(userRepo.findByUsername(INVALID_USERNAME)).thenReturn(null);
    }

    private User createUser() {
        User user = new User();
        Cart cart = new Cart();
        user.setId(VALID_USER_ID);
        user.setUsername(VALID_USERNAME);
        user.setPassword("123");
        user.setCart(cart);
        return user;
    }

    @Test
    public void testCreateUserSuccessful() {
        when(encoder.encode("workingPassword")).thenReturn("workingPassword");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername(VALID_USERNAME);
        r.setPassword("workingPassword");
        r.setConfirmPassword("workingPassword");
        final ResponseEntity<User> response = userController.createUser(r);

        assertSuccessfulResponse(response);
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(VALID_USER_ID.longValue(), u.getId());
        assertEquals(VALID_USERNAME, u.getUsername());
        assertEquals("workingPassword", u.getPassword());
    }

    @Test
    public void testCreateUserPasswordTooShort() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername(VALID_USERNAME);
        r.setPassword("1");
        r.setConfirmPassword("1");
        final ResponseEntity<User> response = userController.createUser(r);

        assertBadRequestResponse(response);
    }

    @Test
    public void testCreateUserPasswordConfirmMismatch() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername(VALID_USERNAME);
        r.setPassword("this password");
        r.setConfirmPassword("doesn't match upper password");
        final ResponseEntity<User> response = userController.createUser(r);

        assertBadRequestResponse(response);
    }

    @Test
    public void testFindUserByNameSuccessful() {
        final ResponseEntity<User> response = userController.findByUserName(VALID_USERNAME);

        assertSuccessfulResponse(response);
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(VALID_USERNAME, u.getUsername());
    }

    @Test
    public void testFindUserByNameDoesNotExist() {
        final ResponseEntity<User> response = userController.findByUserName(INVALID_USERNAME);

        assertNotFoundResponse(response);
    }

    @Test
    public void testFindUserByIdSuccessful() {
        final ResponseEntity<User> response = userController.findById(VALID_USER_ID);

        assertSuccessfulResponse(response);
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(VALID_USER_ID.longValue(), u.getId());
    }

    @Test
    public void testFindUserByIdDoesNotExist() {
        final ResponseEntity<User> response = userController.findById(INVALID_USER_ID);

        assertNotFoundResponse(response);
    }

    private void assertSuccessfulResponse(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
    }

    private void assertBadRequestResponse(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    private void assertNotFoundResponse(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

}