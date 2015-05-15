package com.thebangias.familybudgetclient.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.thebangias.familybudgetclient.R;
import com.thebangias.familybudgetclient.utils.APIUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask authTask = null;

    // UI references.
    private EditText apiRootUrlView;
    private AutoCompleteTextView emailView;
    private EditText passwordView;
    private View progressView;
    private View loginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // retrieve the views
        apiRootUrlView = (EditText) findViewById(R.id.rootApiUrl);
        emailView = (AutoCompleteTextView) findViewById(R.id.email);
        passwordView = (EditText) findViewById(R.id.password);
        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);

        // check shared preferences if the username/password/root URL exist
        String packageName = getString(R.string.package_name);
        SharedPreferences prefs = this.getSharedPreferences(
                packageName, Context.MODE_PRIVATE);
        String savedRootUrl = prefs.getString(packageName + ".apirooturl", null);
        String savedEmail = prefs.getString(packageName + ".email", null);
        String savedPassword = prefs.getString(packageName + ".password", null);

        // set the text fields with the saved preferences (or null if they don't exist)
        apiRootUrlView.setText(savedRootUrl);
        emailView.setText(savedEmail);
        passwordView.setText(savedPassword);

        // if all three do exist, attempt to login, otherwise, proceed to finish setting up the
        // login form
        if (savedRootUrl != null && savedEmail != null && savedPassword != null) {

            attemptLogin();
        } else {

            // Finish setting up the login form.
            // populate autocomplete for the email field
            populateAutoComplete();

            // set up the signin button to attemptLogin on click
            Button emailSignInButton = (Button) findViewById(R.id.sign_in_button);
            emailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });
        }
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
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

            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

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
            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void FinishLogin(boolean isAuthenticated) {
        // if the user is authenticated, save their credentials to the
        // shared preferences, and continue to the home screen
        if (isAuthenticated) {
            String packageName = getString(R.string.package_name);
            SharedPreferences prefs = this.getSharedPreferences(
                    packageName, Context.MODE_PRIVATE);
            Editor prefsEditor = prefs.edit();
            prefsEditor.putString(packageName + ".apirooturl", apiRootUrlView.getText().toString());
            prefsEditor.putString(packageName + ".email", emailView.getText().toString());
            prefsEditor.putString(packageName + ".password", passwordView.getText().toString());
            prefsEditor.apply();

            // open the HomeActivity
            Intent homeScreen = new Intent(this, HomeActivity.class);
            homeScreen.putExtra("auth-msg", "Authentication Passed!");
            startActivity(homeScreen);

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
    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String email;
        private final String password;
        private final String apiRootUrl;

        UserLoginTask(String apiRootUrl, String email, String password) {
            this.email = email;
            this.password = password;
            this.apiRootUrl = apiRootUrl;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // initialize the return boolean value
            boolean isAuthenticated = false;

            try {
                APIUtils utils = new APIUtils(email, password, apiRootUrl);
                // check with the data service if the credentials are correct
                isAuthenticated = utils.CheckAuthentication();
            } catch (Exception ex) {
                // TODO: log any exceptions caught
            }

            return isAuthenticated;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            authTask = null;
            showProgress(false);

            // when complete, send the authentication result back to the LoginActivity
            FinishLogin(success);
        }

        @Override
        protected void onCancelled() {
            authTask = null;
            showProgress(false);
        }
    }

    // Loader callbacks
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        emailView.setAdapter(adapter);
    }
}

