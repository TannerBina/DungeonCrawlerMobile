package hu.ait.android.dungeoncrawler.imports.callers;

/*
 * Class defining the input to the aws lambda function
 * delete_character_function
 * 
 * Requires a character id and a username
 */
public class DeleteCharacterInput {
	private String id;
	private String username;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}
