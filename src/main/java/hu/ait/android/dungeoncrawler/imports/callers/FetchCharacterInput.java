package hu.ait.android.dungeoncrawler.imports.callers;

/*
 * Input for the lambda function fetch_character_function.
 * this function takes in a character id and returns
 * a string containing all character data
 */
public class FetchCharacterInput {
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
