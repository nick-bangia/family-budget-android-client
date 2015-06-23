package com.thebangias.familybudgetclient.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thebangias.familybudgetclient.R;
import com.thebangias.familybudgetclient.model.APIToken;
import com.thebangias.familybudgetclient.model.AllowancesResponse;
import com.thebangias.familybudgetclient.model.LoginResponse;
import com.thebangias.familybudgetclient.model.RefreshAllowancesResponse;
import com.thebangias.familybudgetclient.model.abstractions.APIResponseObject;
import com.thebangias.familybudgetclient.model.abstractions.DataObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * A utility class to communicate with the family budget API
 */
public class APIUtils {

    private String baseUrl;
    private Context context;

    public APIUtils(Context ctx, String baseUrl) {
        this.context = ctx;
        this.baseUrl = baseUrl;
    }

    public APIToken Login(String email, String password) {
        // initialize the token return value
        APIToken apiToken = null;

        // issue a request to /ping to determine if the username/password combo is valid
        HttpURLConnection connection = GetAuthorizedConnection(
            this.context.getResources().getString(R.string.api_login), email, password);
        if (connection != null) {
            LoginResponse response = (LoginResponse) Get(connection, LoginResponse.class);

            if (response != null && response.getStatus().equals("ok")) {
                List<? extends DataObject> data = response.getData();
                List<APIToken> apiTokenData = response.GetStronglyTypedListFrom(data);
                apiToken = apiTokenData.get(0);
            }
        }

        return apiToken;
    }

    public AllowancesResponse GetAllowances() {
        // initialize the AllowanceResponse
        AllowancesResponse response = null;

        // issue a request to the API to get the current allowances
        HttpURLConnection connection = GetTokenizedConnection(this.context.getResources().getString(R.string.api_allowances));
        if (connection != null) {
            // if the connection is not null, issue the Get request
            response = (AllowancesResponse) Get(connection, AllowancesResponse.class);
        }

        return response;
    }

    public RefreshAllowancesResponse RefreshAllowances() {
        // initialize the RefreshAllowancesResponse
        RefreshAllowancesResponse response = null;

        // issue a request to the API to refresh the allowances
        HttpURLConnection connection = GetTokenizedConnection(this.context.getResources().getString(R.string.api_refresh_allowances));
        if (connection != null) {
            // if the connection is not null, issue the Get request
            response = (RefreshAllowancesResponse) Get(connection, RefreshAllowancesResponse.class);
        }

        return response;
    }

    private APIResponseObject Get(HttpURLConnection connection,
                                  Class<? extends APIResponseObject> apiResponseClass) {

        // set the flag indicating that this connection should have a response
        connection.setDoInput(true);

        return GetResponse(connection, apiResponseClass);
    }

    private HttpURLConnection GetAuthorizedConnection(String target, String email, String password) {
        // get the connection from target
        HttpURLConnection connection = GetConnection(target);

        // formulate the Authentication
        String userCreds = email + ":" + password;
        String basicAuthentication =
                "Basic " + new String(Base64.encode(userCreds.getBytes(), Base64.DEFAULT));

        // Set the Authorization header
        connection.setRequestProperty("Authorization", basicAuthentication);

        return connection;
    }

    private HttpURLConnection GetTokenizedConnection(String target) {
        // get the connection from target
        HttpURLConnection connection = GetConnection(target);

        // formulate the token header
        String packageName = this.context.getResources().getString(R.string.package_name);
        SharedPreferences prefs = this.context.getSharedPreferences(
                packageName, Context.MODE_PRIVATE);
        String token = prefs.getString(packageName + ".apiToken", null);

        // set the x_access_token header
        connection.setRequestProperty("x_access_token", token);

        return connection;
    }

    private HttpURLConnection GetConnection(String target) {
        // generate a HttpURLConnection from the provided target
        String uri = baseUrl + target;
        HttpURLConnection connection = null;

        try {
            // get the UrlConnection in the form of HttpURLConnection
            URL url = new URL(uri);
            connection = (HttpURLConnection) url.openConnection();

        } catch (IOException e) {
            // TODO: log exception
            connection = null;
        }

        return connection;
    }

    private APIResponseObject GetResponse(HttpURLConnection connection,
                                          Class<? extends APIResponseObject> apiResponseClass) {

        // initialize the response object
        APIResponseObject response = null;

        // establish the connection, and read back the response using the GSON library and the
        // passed in class
        try {
            // check the response code for the connection & build the APIResponseObject
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // get the Response data in an InputStream
                InputStream stream = new BufferedInputStream(connection.getInputStream());

                // get the response data into a string, and convert it from JSON to the
                // APIResponseObject class using the GSON library
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
                response = gson.fromJson(GetResponseData(stream), apiResponseClass);

            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                // if the response code is 401 - Unauthorized, set up the response object accordingly
                response = apiResponseClass.newInstance();
                response.setStatus("failure");
                response.setReason("401 - Unauthorized");
            }

        } catch (IOException e) {
            // TODO: log the exception
            response = null;
        } catch (InstantiationException | IllegalAccessException e) {
            // TODO: Log the exception
        }

        return response;
    }

    private String GetResponseData(InputStream stream) {
        char[] buffer = new char[100];
        int length = 0;
        StringBuilder strBuilder = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(stream);
        String retString = null;


        try {
            // loop through the inputStream w/ InputStreamReader,
            // and write the buffer to the strBuilder
            while ((length = reader.read(buffer, 0, buffer.length)) != -1) {
                strBuilder.append(buffer, 0, length);
            }

            // convert the builder to a string
            retString = strBuilder.toString();

        } catch (IOException e) {
            // TODO: log the exception
            retString = null;
        }

        return retString;
    }
}
