package hu.ait.android.dungeoncrawler.imports.callers;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

/*
 * Interacts with the function login_function
 */
public interface LoginService {
	@LambdaFunction(functionName="login_function")
	Output login(LoginInput input);
}
