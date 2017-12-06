package hu.ait.android.dungeoncrawler.imports.callers;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface CreateCharacterService {
    @LambdaFunction(functionName = "create_character_function")
    Output createCharacter(CreateCharacterInput input);
}
