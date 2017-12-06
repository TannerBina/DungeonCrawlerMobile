package hu.ait.android.dungeoncrawler.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunctionException;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;

import hu.ait.android.dungeoncrawler.R;
import hu.ait.android.dungeoncrawler.data.User;
import hu.ait.android.dungeoncrawler.imports.callers.CreateUserInput;
import hu.ait.android.dungeoncrawler.imports.callers.CreateUserService;
import hu.ait.android.dungeoncrawler.imports.callers.LoginInput;
import hu.ait.android.dungeoncrawler.imports.callers.LoginService;
import hu.ait.android.dungeoncrawler.imports.callers.Output;
import hu.ait.android.dungeoncrawler.imports.util.Messages;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "DungeonCrawlerPrefs";
    public static final String USERNAME_KEY = "username";
    public static final String PASSWORD_KEY = "password";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(false);
            }
        });
        Button mEmailRegisterButton = (Button) findViewById(R.id.email_register_button);
        mEmailRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(true);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String username = settings.getString(USERNAME_KEY, null);
        String password = settings.getString(PASSWORD_KEY, null);
        if (username != null && password != null){
            mUsernameView.setText(username);
            mPasswordView.setText(password);
            attemptLogin(false);
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin(boolean createAccount) {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }
        else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_email));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(username, password, createAccount);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isUsernameValid(String email) {
        return email.length() > 4 && email.length() < 26;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4 && password.length() < 26;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;
        private final boolean mCreateAccount;
        private Output result;

        private CognitoCachingCredentialsProvider credentialsProvider;
        private LambdaInvokerFactory factory;

        UserLoginTask(String username, String password, boolean createAccount) {
            mUsername = username;
            mPassword = password;
            mCreateAccount = createAccount;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Initialize the Amazon Cognito credentials provider
            credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),
                    "us-east-2:4afff1a5-f1a5-49fb-99c3-d7167ff61afe", // Identity pool ID
                    Regions.US_EAST_2 // Region
            );

            factory = new LambdaInvokerFactory(getApplicationContext(),
                    Regions.US_EAST_2, credentialsProvider);

            if (!mCreateAccount) {
                LoginInput input = new LoginInput();
                input.setPassword(mPassword);
                input.setUsername(mUsername);

                try {
                    final LoginService login = factory.build(LoginService.class);
                    result = login.login(input);
                } catch (LambdaFunctionException e){
                    return false;
                }

                if (result.getMessage().contains(Messages.ERROR_TAG)){
                    return false;
                }
            } else {
                CreateUserInput input = new CreateUserInput();
                input.setUsername(mUsername);
                input.setPassword_1(mPassword);
                input.setPassword_2(mPassword);

                try {
                    final CreateUserService createUser = factory.build(CreateUserService.class);
                    result = createUser.createUser(input);
                } catch (LambdaFunctionException e){
                    return false;
                }

                if (result.getMessage().contains(Messages.ERROR_TAG)){
                    return false;
                }
            }

            User.getInstance().setCredentials(mUsername, mPassword);
            boolean res = User.getInstance().setCharacters(result.getData(), factory);
            if (!res){
                Toast.makeText(getApplicationContext(),
                        R.string.error_unexpected,
                        Toast.LENGTH_LONG).show();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(USERNAME_KEY, mUsername);
                editor.putString(PASSWORD_KEY, mPassword);
                editor.commit();

                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, CharacterListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                try {
                    Messages.Error error = Messages.getErrorMessage(result.getMessage());
                    switch (error){
                        case INCORRECT_PASSWORD:
                            mPasswordView.setError(getString(R.string.error_incorrect_password));
                            mPasswordView.requestFocus();
                            break;
                        case INCORRECT_USERNAME:
                            mUsernameView.setError(getString(R.string.error_incorrect_username));
                            mUsernameView.requestFocus();
                            break;
                        case USERNAME_EXISTS:
                            mUsernameView.setError(getString(R.string.error_username_exists));
                            mUsernameView.requestFocus();
                            break;
                    }
                } catch (Exception e){
                    Toast.makeText(getApplicationContext(),
                            R.string.error_unrecognized,
                            Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

