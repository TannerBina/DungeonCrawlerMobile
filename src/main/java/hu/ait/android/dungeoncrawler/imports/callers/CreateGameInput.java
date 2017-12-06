package hu.ait.android.dungeoncrawler.imports.callers;

/*
 * Class defining the input to the aws lambda
 * function create_game_function
 * 
 * requires name (gameName), username(host),
 * and a password to join the game
 */
public class CreateGameInput {
	private String name;
	private String username;
	private String password;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
