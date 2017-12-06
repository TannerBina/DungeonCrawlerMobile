package hu.ait.android.dungeoncrawler.imports.callers;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

/*
 * Interface to define interaction with the
 * lambda function delete_user_function
 */
public interface DeleteCharacterService {
	@LambdaFunction(functionName="delete_character_function")
	Output deleteCharacter(DeleteCharacterInput input);
}
