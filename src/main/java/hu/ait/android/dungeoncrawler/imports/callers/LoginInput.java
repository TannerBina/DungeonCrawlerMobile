package hu.ait.android.dungeoncrawler.imports.callers;

/*
 * Holds information for a call to the 
 * lambda function login_function
 * requires a username and password
 * 
 * returns success and character list if successful,
 * else returns an error message
 */
public class LoginInput {
	private String username;
	private String password;
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}
