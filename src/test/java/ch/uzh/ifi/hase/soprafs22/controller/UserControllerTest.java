package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.AdditionalMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;


  @Test
  public void createUser_validInput_userCreated() throws Exception {
      // given post 201     post 1 yess working
      User user = new User();
      user.setPassword("password");
      user.setUsername("username");

      // when/then -> do the request + validate the result
      MockHttpServletRequestBuilder postRequest = post("/users")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(user));

      // then
      mockMvc.perform(postRequest)
              .andExpect(status().isCreated());

  }

    @Test
    public void invalidUsernameInput_whenPostUser_thenReturnConflict() throws Exception {
        // given post 409     post 2   no
        User user = new User();
        user.setPassword("password");
        user.setUsername("username");

        User user2 = new User();
        user2.setPassword("password");
        user2.setUsername("username");


        when(userService.createUser(user)).thenThrow(new ResponseStatusException(HttpStatus.CREATED));
        when(userService.createUser(user2)).thenThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user));

        MockHttpServletRequestBuilder postRequest2 = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user2));

        // then
        mockMvc.perform(postRequest)
               .andExpect(status().isCreated());
        mockMvc.perform(postRequest2)
                .andExpect(status().isConflict());

    }



    @Test
    public void validId_whenGetUserId_thenReturnUser() throws Exception {
        // given get 200 OK     get 1    WORKING
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setlogged_in(true);

        //List<User> allUser = Collections.singletonList(user);
        //given(userService.getUserById(user.getId())).willReturn(user);
        //given(userService.getUsers()).willReturn(allUser);

        when(userService.getUserById(user.getId())).thenReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/users/{Id}", user.getId()).contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.logged_in", is(user.getlogged_in())));


    }

    @Test
    public void invalidId_whenGetUserId_thenReturnUser() throws Exception {
        // given get 200 OK     get 1    WORKING
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setlogged_in(true);

        //List<User> allUser = Collections.singletonList(user);
        //given(userService.getUserById(user.getId())).willReturn(user);
        //given(userService.getUsers()).willReturn(allUser);

        when(userService.getUserById(user.getId())).thenReturn(user);
        when(userService.getUserById(user.getId()+1)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/users/{Id}", user.getId()+1).contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());

    }




    @Test
    public void validInput_whenPutUserId_thenReturnNoContent() throws Exception {
        // given post 204 no content, update User     put 1     yess
        User user = new User();
        user.setId(1L);
        user.setPassword("Test User");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("Test User");
        userPostDTO.setUsername("testUsername");

        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users/{Id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());

    }

    @Test
    public void invalidId_whenPutUserId_thenReturnNotFound() throws Exception {
        // given put 404 not found      put 2     no
        User user = new User();
        user.setId(1L);
        user.setPassword("Test User");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);


//        when(userService.updateUser(Matchers.eq(user))).thenThrow(new ResponseStatusException(HttpStatus.NO_CONTENT));
//        when(userService.updateUser(AdditionalMatchers.not(Matchers.eq(user)))).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
//        when(userService.updateUser(AdditionalMatchers.not(Mockito.eq(user)))).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
//        //anything but not "ejb"
//        when(userService.updateUser(not(eq(user))));

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testUsername");

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users/{Id}", user.getId()+1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound());

    }













  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   * 
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}
//
///users POST username <string>, password <string> Body 201 User (**),
//        Authorization (*) add User
//        /users POST username <string>, password <string> Body 409 Error: reason<string> add User failed because
//        username already exists
//        /users/{userId} GET userId<long> Query 200 User (**) retrieve user profile with
//        userId
//        /users/{userId} GET userId<long> Query 404 Error: reason<string> user with userId was not
//        found
//        /users/{userId} (*) GET userId<long> Query 401 Error: reason<string> Not authorized
//        /users/{userId} PUT User Body 204 update user profile
//        /users/{userId} PUT User Body 404 Error: reason<string> user with userId was not
//        found
//        /users/{userId} (*) PUT User Body 401 Error: reason<string> Not authorized
