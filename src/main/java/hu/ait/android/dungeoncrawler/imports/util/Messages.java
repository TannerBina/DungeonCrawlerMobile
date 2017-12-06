package hu.ait.android.dungeoncrawler.imports.util;

import java.util.Scanner;

/*
 * A message class which defines the error
 * and success messages that can be returned from calls
 * to aws lambda functions. also contains parse error and
 * success message functions to return the necessary error
 */
public class Messages {

	//the list of errors that can be returned
	public enum Error{
		PASSWORD_MATCH, USERNAME_EXISTS, USERNAME_TOO_SHORT,
		PASSWORD_TOO_SHORT, USERNAME_TOO_LONG, PASSWORD_TOO_LONG,
		INCORRECT_PARAMETERS, INCORRECT_PASSWORD, INCORRECT_USERNAME,
		CHARACTER_DOES_NOT_EXIST, GAME_NAME_EXISTS, GAME_NAME_TOO_LONG,
		GAME_NAME_TOO_SHORT
	}
	
	public static final String[] ERR_MESSAGES = {
		"Entered Passwords do not match",
		"Username already exists",
		"Username too short (5-25)",
		"Password too short (5-25)",
		"Username too long (5-25)",
		"Password too long (5-25)",
		"INCORRECT PARAMS : CODING ERROR : PLEASE REPORT TO tanner.bina@richmond.edu",
		"Incorrect password",
		"Username does not exist",
		"Character does not exist",
		"Game name already exists",
		"Game name too long (5-25)",
		"Game name too short (5-25)"
		
	};
	
	//enum of success messages that can be returned
	public enum Success{
		CREATE_SUCCESS, LOGIN_SUCCESS, CHARACTER_FETCHED,
		CHARACTER_DELETED, GAME_DELETED, GAME_CREATED,
		GAMES_FETCHED
	}
	
	//the tags for a success and error message
	public static final String SUCCESS_TAG = "SUC";
	public static final String ERROR_TAG = "ERR";
	
	/*
	 * Returns the corresponding error string for
	 * the given error
	 */
	public static String getErrorString(Error err) {
		return ERR_MESSAGES[err.ordinal()];
	}
	
	/*
	 * Takes in an error message string and returns
	 * the resulting error
	 */
	public static Error getErrorMessage(String input) throws Exception {
		//set deliminator to the lambda function deliminator
		Scanner s = new Scanner(input);
		s.useDelimiter(Constants.LAMBDA_DELIMINATOR);
		
		//get the tag
		String tag = s.next();
		
		//if its not the error tag throw exception
		if (!tag.equals(ERROR_TAG)) {
			s.close();
			throw new Exception("Incorrect Error Tag : " + tag);
		}
		
		//get the message and return corresonding error
		String message = s.next();
		
		s.close();
		
		return Error.valueOf(message);
	}
	
	/*
	 * Takes in a success message string and returns
	 * the resulting success message
	 */
	public static Success getSuccessMessage(String input) throws Exception {
		//set deliminator to lambda delim
		Scanner s = new Scanner(input);
		s.useDelimiter(Constants.LAMBDA_DELIMINATOR);
		
		//get the tag
		String tag = s.next();
		
		//if its not the error tag throw exception
		if (!tag.equals(SUCCESS_TAG)) {
			s.close();
			throw new Exception("Incorrect Success Tag : " + tag);
		}
		
		//get and return the message value
		String message = s.next();
		
		//close scanner
		s.close();
		return Success.valueOf(message);
	}
}

