package hu.ait.android.dungeoncrawler.imports.callers;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface DeleteGameService {
	@LambdaFunction(functionName="delete_game_function")
	Output deleteGame(DeleteGameInput input);
}
