package com.thebangias.familybudgetclient.utils;

import android.util.Base64;

import com.google.gson.Gson;
import com.thebangias.familybudgetclient.model.Ping;
import com.thebangias.familybudgetclient.model.abstractions.APIResponseObject;
import com.thebangias.familybudgetclient.model.abstractions.DataObject;
import com.thebangias.familybudgetclient.model.PingResponse;

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
    private String email;
    private String password;

    public APIUtils(String email, String password, String baseUrl) {
        this.baseUrl = baseUrl;
        this.email = email;
        this.password = password;
    }

    public boolean CheckAuthentication() {
        // initialize the boolean return value
        boolean isValidCredentials = false;

        // issue a request to /ping to determine if the username/password combo is valid
        HttpURLConnection connection = GetAuthorizedConnection("/ping");
        if (connection != null) {
            PingResponse response = (PingResponse) Get(connection, PingResponse.class);

            if (response != null && response.getStatus().equals("ok")) {
                List<? extends DataObject> data = response.getData();
                List<Ping> pingData = response.GetStronglyTypedListFrom(data);
                isValidCredentials = pingData.get(0).getIsAuthorized();
            }
        }

        return isValidCredentials;
    }

    private APIResponseObject Get(HttpURLConnection connection,
                                  Class<? extends APIResponseObject> apiResponseClass) {

        // set the flag indicating that this connection should have a response
        connection.setDoInput(true);

        return GetResponse(connection, apiResponseClass);
    }

    private HttpURLConnection GetAuthorizedConnection(String target) {
        String uri = baseUrl + target;
        HttpURLConnection connection = null;

        try {
            // get the UrlConnection in the form of HttpURLConnection
            URL url = new URL(uri);
            connection = (HttpURLConnection) url.openConnection();

            // formulate the Authentication
            String userCreds = this.email + ":" + this.password;
            String basicAuthentication =
                    "Basic " + new String(Base64.encode(userCreds.getBytes(), Base64.DEFAULT));

            // Set the Authorization header
            connection.setRequestProperty("Authorization", basicAuthentication);
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
            // establish the connection
            //connection.connect();

            // check the response code for the connection & build the APIResponseObject
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // get the Response data in an InputStream
                InputStream stream = new BufferedInputStream(connection.getInputStream());

                // get the response data into a string, and convert it from JSON to the
                // APIResponseObject class using the GSON library
                response = new Gson().fromJson(GetResponseData(stream), apiResponseClass);

            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                // if the response code is 401 - Unauthorized, set up the response object accordingly
                response = apiResponseClass.newInstance();
                response.setStatus("failure");
                response.setReason(connection.getResponseMessage());
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
