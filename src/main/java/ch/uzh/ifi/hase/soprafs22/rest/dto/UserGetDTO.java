package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;

import java.util.Date;

public class UserGetDTO {

  private Long id;
  //private String password;
  private String username;
  //private UserStatus status;
  private Date creation_date;
  private boolean logged_in;
  private Date birthday;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }


  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }


    public Date getcreation_date() {
        return creation_date;
    }

    public void setcreation_date(Date creation_date) {
        this.creation_date = creation_date;
    }

    public boolean getlogged_in() {
        return logged_in;
    }

    public void setlogged_in(boolean logged_in) {
        this.logged_in = logged_in;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
