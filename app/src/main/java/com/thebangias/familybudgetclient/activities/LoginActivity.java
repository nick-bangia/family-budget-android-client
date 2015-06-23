package com.thebangias.familybudgetclient.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.thebangias.familybudgetclient.R;
import com.thebangias.familybudgetclient.model.APIToken;
import com.thebangias.familybudgetclient.utils.APIUtils;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask authTask = null;

    // UI references.
    private EditText apiRootUrlView;
    private AutoCompleteTextView emailView;
    private EditText passwordView;
    private View progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // retrieve the views
        apiRootUrlView = (EditText) findViewById(R.id.rootApiUrl);
        emailView = (AutoCompleteTextView) findViewById(R.id.email);
        passwordView = (EditText) findViewById(R.id.password);
        progressView = findViewById(R.id.login_progress);

        // check shared preferences if the username/password/root URL exist
        String packageName = getString(R.string.package_name);
        SharedPreferences prefs = this.getSharedPreferences(
                packageName, Context.MODE_PRIVATE);
        String savedRootUrl = prefs.getString(packageName + ".apirooturl", null);
        String savedEmail = prefs.getString(packageName + ".email", null);

        // set the text fields with the saved preferences (or null if they don't exist)
        apiRootUrlView.setText(savedRootUrl);
        emailView.setText(savedEmail);

        // Finish setting up the login form.
        // set up the signin button to attemptLogin on click
        Button emailSignInButton = (Button) findViewById(R.id.sign_in_button);
        emailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (authTask != null) {
            return;
        }

        // Reset errors.
        emailView.setError(null);
        passwordView.setError(null);
        apiRootUrlView.setError(null);

        // Store values at the time of the login attempt.
        String apiRootUrl = apiRootUrlView.getText().toString();
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_field_required));
            focusView = emailView;
            cancel = true;
        } else if(!isEmailValid(email)) {
            emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
            cancel = true;
    }

        // Check for a valid root URL
        if (TextUtils.isEmpty(apiRootUrl)) {
            apiRootUrlView.setError(getString(R.string.error_field_required));
            focusView = apiRootUrlView;
            cancel = true;
        } else if (!isUriValid(apiRootUrl)) {
            apiRootUrlView.setError(getString(R.string.error_invalid_apiRootUri));
            focusView = apiRootUrlView;
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
            authTask = new UserLoginTask(apiRootUrl, email, password);
            authTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 6;
    }

    private boolean isUriValid(String uri) {
        return Patterns.WEB_URL.matcher(uri).matches();
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void FinishLogin(APIToken token) {
        // if the user is authenticated, save their credentials to the
        // shared preferences, and continue to the home screen
        if (token != null) {
            String packageName = getString(R.string.package_name);
            SharedPreferences prefs = this.getSharedPreferences(
                    packageName, Context.MODE_PRIVATE);
            Editor prefsEditor = prefs.edit();
            prefsEditor.putString(packageName + ".apirooturl", apiRootUrlView.getText().toString());
            prefsEditor.putString(packageName + ".email", emailView.getText().toString());
            prefsEditor.putString(packageName + ".apiToken", token.getAccess_token());
            prefsEditor.apply();

            // start the AllowancesActivity
            Intent allowances = new Intent(this, AllowancesActivity.class);
            startActivity(allowances);
            finish();

        } else {
            // if the user is not authenticated, show an error on the email & password fields
            // indicating so
            emailView.setError(getString(R.string.error_invalid_email_password));
            passwordView.setError(getString(R.string.error_invalid_email_password));
            emailView.requestFocus();
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class UserLoginTask extends AsyncTask<Void, Void, APIToken> {

        private final String email;
        private final String password;
        private final String apiRootUrl;

        UserLoginTask(String apiRootUrl, String email, String password) {
            this.email = email;
            this.password = password;
            this.apiRootUrl = apiRootUrl;
        }

        @Override
        protected APIToken doInBackground(Void... params) {
            // initialize the return token
            APIToken apiToken = null;

            try {
                APIUtils utils = new APIUtils(LoginActivity.this, apiRootUrl);
                // check with the data service if the credentials are correct
                apiToken = utils.Login(email, password);
            } catch (Exception ex) {
                // TODO: log any exceptions caught
            }

            return apiToken;
        }

        @Override
        protected void onPostExecute(final APIToken token) {
            authTask = null;
            showProgress(false);

            // when complete, send the authentication result back to the LoginActivity
            FinishLogin(token);
        }

        @Override
        protected void onCancelled() {
            authTask = null;
            showProgress(false);
        }
    }
}

