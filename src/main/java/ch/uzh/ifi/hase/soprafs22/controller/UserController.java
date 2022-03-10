package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/users/{Id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getUserById(@PathVariable("Id") Long Id) {
    // fetch all users in the internal representation
    User user = userService.getUserById(Id);
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
  }

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers() {
      // fetch all users in the internal representation
      List<User> users = userService.getUsers();
      List<UserGetDTO> userGetDTOs = new ArrayList<>();

      // convert each user to the API representation
      for (User user : users) {
          userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
      }
      return userGetDTOs;
  }

    @PutMapping("/users/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO loginUser(@RequestBody UserPostDTO userPostDTO) {

        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        User loggedInUser = userService.loginUser(userInput);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(loggedInUser);

    }

    @PutMapping("/users/logout")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO logoutUser(@RequestBody UserPostDTO userPostDTO) {

        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        User loggedInUser = userService.logoutUser(userInput);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(loggedInUser);

    }

    @PutMapping("/users/{ID}/logout")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO logoutUser(@RequestBody Long ID) {

        User loggedInUser = userService.getUserById(ID);
        userService.logoutUser(loggedInUser);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(loggedInUser);

    }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    User createdUser = userService.createUser(userInput);

    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
  }

    @PutMapping("/users/{Id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updateUser(@RequestBody User user) {
        userService.updateUser(user);
    }

    @PutMapping("/users/username/{Id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updateUsername(@RequestBody User user) {
        userService.updateUsername(user);
    }

    @PutMapping("/users/bday/{Id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updateUserBday(@RequestBody User user) {
        userService.updateUserBday(user);
    }

}
