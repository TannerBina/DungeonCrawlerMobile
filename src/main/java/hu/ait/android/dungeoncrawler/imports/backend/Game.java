package hu.ait.android.dungeoncrawler.imports.backend;

/*
 * A holder for information about a given game
 */
public class Game {

	private String name;
	private String password;
	
	public Game(String name, String password) {
		this.name = name;
		this.password = password;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
