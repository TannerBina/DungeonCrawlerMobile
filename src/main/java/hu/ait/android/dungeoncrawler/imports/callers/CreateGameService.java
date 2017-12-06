package hu.ait.android.dungeoncrawler.imports.callers;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

/*
 * Interface to define interaction with the
 * lambda function create_game_function
 */
public interface CreateGameService {
	@LambdaFunction(functionName="create_game_function")
	Output createGame(CreateGameInput input);
}