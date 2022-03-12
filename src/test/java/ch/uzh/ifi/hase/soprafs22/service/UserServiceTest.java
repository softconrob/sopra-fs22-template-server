package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  private User testUser;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testUser = new User();
    testUser.setId(1L);
    testUser.setPassword("password");
    testUser.setUsername("testUsername");
    testUser.setlogged_in(false);

    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
  }

    //create aka post

  @Test
  public void createUser_validInputs_success() {
    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    User createdUser = userService.createUser(testUser);

    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getPassword(), createdUser.getPassword());
    assertEquals(testUser.getUsername(), createdUser.getUsername());

  }



  @Test
  public void createUser_duplicateName_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    //Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  public void createUser_duplicateInputs_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    //Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  //login

  @Test
  public void loginUser_noPassword_throwsException() {
      // given -> a first user has already been created
      User user = userService.createUser(testUser);

      // set no password
      user.setPassword(null);

      // is thrown
      assertThrows(ResponseStatusException.class, () -> userService.loginUser(user));
  }

  @Test
  public void loginUser_falsePassword_throwsException() {
      // given -> a first user has already been created
      User user = userService.createUser(testUser);

      // false password
      user.setPassword("not the password");

      // is thrown
      assertThrows(ResponseStatusException.class, () -> userService.loginUser(user));
  }

  @Test
  public void loginUser_noUsername_throwsException() {
      // given -> a first user has already been created
      User user = userService.createUser(testUser);

      // set no username
      user.setUsername(null);

      // is thrown
      assertThrows(ResponseStatusException.class, () -> userService.loginUser(user));
  }

  @Test
  public void loginUser_falseUsername_throwsException() {
      // given -> a first user has already been created
      User user = userService.createUser(testUser);

      // set false username
      user.setUsername("not the username");

      // is thrown
      assertThrows(ResponseStatusException.class, () -> userService.loginUser(user));
  }

  @Test
  public void loginUser_validInputs_success() {
      // given -> a first user has already been created
      User user = userService.createUser(testUser);

      // when
      Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

      userService.loginUser(user);

      Mockito.verify(userRepository, Mockito.times(2)).save(Mockito.any());

      assertEquals(user.getlogged_in(), true);
  }

  @Test
  public void loginUser_userIsAlreadyLoggedIn_throwsException() {
      // given -> a first user has already been created
      User user = userService.createUser(testUser);
      user.setlogged_in(true);

      // is thrown
      assertThrows(ResponseStatusException.class, () -> userService.loginUser(user));
  }



    //logout
  @Test
  public void logoutUser_validInputs_success() {
      // given -> a first user has already been created
      User user = userService.createUser(testUser);
      user.setlogged_in(true);

      // when
      Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

      userService.logoutUser(user);

      Mockito.verify(userRepository, Mockito.times(2)).save(Mockito.any());

    //user successfully logged out with logged out = false = offline
      assertEquals(user.getlogged_in(), false);
  }

  @Test
  public void logoutUser_alreadyLoggedOut_throwsException() {
      // given -> a first user has already been created
      User user = userService.createUser(testUser);

      // set logged in to false = offline
      user.setlogged_in(false);

      // is thrown
      assertThrows(ResponseStatusException.class, () -> userService.logoutUser(user));
  }



  // get User by Id
  @Test
  public void getUser_validInputs_success() {
      // given -> a first user has already been created
      User user = userService.createUser(testUser);

      Mockito.when(userRepository.findUserById(Mockito.any())).thenReturn(testUser);

      User returnedUser = userService.getUserById(user.getId());

      assertEquals(testUser, returnedUser);
    }

    @Test
    public void getUser_invalidInputs_throwsException() {
      // given -> a first user has already been created
        User user = userService.createUser(testUser);

        Mockito.when(userRepository.findUserById(2L)).thenReturn(testUser);

        // is thrown
        assertThrows(ResponseStatusException.class, () -> userService.getUserById(user.getId()));
    }



    // update User aka put

  @Test
  public void updateUser_validInputs_success() {
      //set borthday to now
      Date birthday = new Date();
      String username = "new username";

      // given -> a first user has already been created
      User user = userService.createUser(testUser);
      user.setUsername(username);
      user.setBirthday(birthday);

      Mockito.when(userRepository.findUserById(testUser.getId())).thenReturn(testUser);

      userService.updateUser(user);

      User updatedUser = user;

      Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

      assertEquals(testUser.getId(), updatedUser.getId());
      assertEquals(username, updatedUser.getUsername());
      assertEquals(birthday, updatedUser.getBirthday());

  }

  @Test
  public void updateUser_usernameAlreadyExists_throwsException() {
      // given -> a first user has already been created
      userService.createUser(testUser);

      // when -> setup additional mocks for UserRepository
      //Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(testUser);
      Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

      // then -> attempt to create second user with same user -> check that an error
      // is thrown
      assertThrows(ResponseStatusException.class, () -> userService.updateUser(testUser));

  }



}
