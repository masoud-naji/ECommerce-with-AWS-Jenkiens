package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepo =mock(UserRepository.class);
    private CartRepository cartRepo =mock(CartRepository.class);
    private BCryptPasswordEncoder encoder =mock(BCryptPasswordEncoder.class);

    @Before
    public void setup(){
        userController =new UserController();
        TestUtils.injectObjects(UserController,"userRepository",userRepo);
        TestUtils.injectObjects(UserController,"cartRepository",cartRepo);
        TestUtils.injectObjects(UserController,"bCryptPasswordEncoder",encoder);

        User user = new User();
        user.setId(0);
        when(userRepo.findById(0L)).thenReturn(java.util.Optional.of(user));
        }


    @Test
    public void create_user_happy_path() throws Exception{
        when(encoder.encode("testPassword")).thenReturn("this is Hashed");
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0,user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("this is Hashed", user.getPassword());

    }

    @Test
    public void create_user_bad_request() throws Exception{
        when(encoder.encode("testPassword")).thenReturn("this is Hashed");
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("test");
        createUserRequest.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

    }


/*    @Test
    public void verify_password_length_check()throws Exception{
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("abcde");
        r.setConfirmPassword("abcde");

        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }


    @Test
    public void verify_confirm_password_check() throws Exception{
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword1");

        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }*/

    @Test
    public void not_found_user_by_name() {
        final ResponseEntity<User> response = userController.findByUserName("anotherUser");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void not_found_user_by_id() {
        final ResponseEntity<User> response = userController.findById(33L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
    @Test
    public void verify_find_user_by_id(){
        final ResponseEntity<User> response = userController.findById(0L);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertEquals(0, user.getId());
    }

    private ResponseEntity<User> createNewUser(){
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");
                          
        return userController.createUser(createUserRequest);
    }



}
