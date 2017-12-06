package hu.ait.android.dungeoncrawler.imports.callers;

/*
 * Class defining the input to the aws lambda function
 * create_user_function.
 * 
 * Requires two password attempts(should match)
 * and a username
 */
public class CreateUserInput {
	private String password_1;
	private String password_2;
	private String username;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword_2() {
		return password_2;
	}
	public void setPassword_2(String password_2) {
		this.password_2 = password_2;
	}
	
	public String getPassword_1() {
		return password_1;
	}
	public void setPassword_1(String password_1) {
		this.password_1 = password_1;
	}
}

