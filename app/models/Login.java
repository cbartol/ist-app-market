package models;

import javax.persistence.Id;

import play.db.ebean.Model;

public class Login extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	public String username;

    public static User authenticate(String username) {
      return User.get(username);
    }
    
    public String validate() {
    	if(username==null || username.isEmpty()) {
    		return "Please enter a username";
    	}
    	if(authenticate(username) == null) {
    		return "Invalid User";
    	}
    	return null;
    }

	public static void logout(String username) {
	}
    
}
