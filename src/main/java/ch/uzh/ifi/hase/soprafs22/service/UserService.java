package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User getUserById(Long userId) {
        checkIfUserIdExists(userId);
        return userRepository.findUserById(userId);

  }

  public User loginUser(User userInput){
      User existingUser = userRepository.findByUsername(userInput.getUsername());
      if (existingUser == null){
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with this username");
      }
      if (!userInput.getPassword().equals(existingUser.getPassword())){
          throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Password incorrect");
      }
      existingUser.setlogged_in(true);
      userRepository.save(existingUser);
      userRepository.flush();
      return existingUser;
  }

    public User logoutUser(User userInput){
        User currentUser = userRepository.findByToken(userInput.getToken());
        if (currentUser != null && currentUser.getlogged_in()){
            currentUser.setlogged_in(false);
            userRepository.save(currentUser);
            userRepository.flush();
            return currentUser;
        }
        else{
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Could not log out user");
        }

    }

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setlogged_in(true);

    checkIfUserExists(newUser);

    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();


    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  public void updateUser(User user){
      checkIfUserExists(user);
      Long id = user.getId();
      User oldUser = userRepository.findUserById(id);
      oldUser.setBirthday(user.getBirthday());
      oldUser.setUsername(user.getUsername());
  }

  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is not unique");
    }
  }

    private void checkIfUserIdExists(Long userId) {
        User userById = userRepository.findUserById(userId);

        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
        if (userById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Id not found");
        }
    }
}
