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

import static org.junit.Assert.*;

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
        TestUtils.injectObjects(userController,"userRepository",userRepo);
        TestUtils.injectObjects(userController,"CartRepository",cartRepo);
        TestUtils.injectObjects(userController,"BCryptPasswordEncoder",encoder);

        User user = new User();
        user.setId(0);
        when(userRepo.findById(0L)).thenReturn(java.util.Optional.of(user));
        }


    @Test
    public void create_user_happy_path() throws Exception{
        when(encoder.encode("testPassword")).thenReturn("thisisHashed");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");
        final ResponseEntity<User> response =userController.createUser(r);
        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0,u.getId());
        assertEquals("test",u.getUsername());
        assertEquals("thisisHashed",u.getPassword());

    }

    @Test
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
    }

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

}
