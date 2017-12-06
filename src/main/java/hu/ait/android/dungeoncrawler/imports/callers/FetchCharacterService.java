package hu.ait.android.dungeoncrawler.imports.callers;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

/*
 * Interacts with fetch_character_function
 */
public interface FetchCharacterService {
	@LambdaFunction(functionName="fetch_character_function")
	Output fetchCharacter(FetchCharacterInput input);
}
