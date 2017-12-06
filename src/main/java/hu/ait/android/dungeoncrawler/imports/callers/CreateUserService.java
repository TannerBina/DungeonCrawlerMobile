package hu.ait.android.dungeoncrawler.imports.callers;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

/*
 * Interface to define interaction with the
 * lambda function create_user_function
 */
public interface CreateUserService {
	@LambdaFunction(functionName="create_user_function")
	Output createUser(CreateUserInput input);
}
