package hu.ait.android.dungeoncrawler.imports.callers;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface FetchAllGamesService {
	@LambdaFunction(functionName="fetch_all_games_function")
	Output fetchAllGames(FetchAllGamesInput input);
}
