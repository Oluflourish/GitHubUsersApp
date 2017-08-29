package com.example.android.githubusersproject;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving user data from the GitHub API.
 */
public final class QueryUtils {
    /** Tag for the log messages */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();


    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    /**
     * Return a list of {@link User} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<User> extractFeatureFromJson(String userJSON) {


        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(userJSON)) {
            /** Beginning of dummy data code **/
            // Add dummy user
            // Create an empty ArrayList that we can start adding users to
            /*List<User> users = new ArrayList<>();
            users.add(new User(0, "Surname Firstname 1", "name@domain.com", "Software Developer (Web and Mobile apps) and Electrical/Electronics Engineer.", 120, 37, 15, "", ""));
            users.add(new User(1, "Surname Firstname 2", User.EMPTY_EMAIL_TEXT, "Software Developer (Web and Mobile apps) and Electrical/Electronics Engineer.", 100, 53, 40, "", ""));
            users.add(new User(2, "Surname Firstname 2", User.EMPTY_EMAIL_TEXT, "null", 70, 163, 80, "", ""));
            return users;*/
            /** End of dummy data code **/

            return null;
        }

        // Create an empty ArrayList that we can start adding users to
        List<User> users = new ArrayList<>();


        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create  a JSONObject from the SAMPLE_JSON_RESPONSE string
            JSONObject baseJsonResponse = new JSONObject(userJSON);

            // Extract array associated with the key called "features",
            // which represents a list of features (or users).
            JSONArray userArray = baseJsonResponse.getJSONArray("items");

            // For each user in the userArray, create an User object
            for(int i = 0; i < userArray.length(); i++) {
                // All of our code in the loop
                JSONObject currentUser = userArray.getJSONObject(i);

                long userID = currentUser.getLong("id");
                String avatar_url = currentUser.getString("avatar_url");
                String user_url = currentUser.getString("url");
                String user_html_url = currentUser.getString("html_url");

                // Extract each user details
                // Perform HTTP request to the URL and receive a JSON response back
                String jsonResponse = null;
                try {
                    jsonResponse = makeHttpRequest(createUrl(user_url));
                    // jsonResponse = makeHttpRequest(createUrl(user_url+"?access_token=85a2e91c541c5a4e31698dca71e0fd450fdc52b4"));
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing input stream", e);
                }
                // Create  a JSONObject from the SAMPLE_JSON_RESPONSE string
                JSONObject userJsonResponse = new JSONObject(jsonResponse);
                String userFullName = userJsonResponse.getString("name");
                String email = userJsonResponse.getString("email");
                String bio = userJsonResponse.getString("bio").trim();
                int followers = userJsonResponse.getInt("followers");
                int following = userJsonResponse.getInt("following");
                int repos = userJsonResponse.getInt("public_repos");

                if (TextUtils.isEmpty(userFullName) || userFullName.equals("null")) {
                    userFullName = userJsonResponse.getString("login");
                }
                if (TextUtils.isEmpty(email) || email.equals("null")) {
                    email = User.EMPTY_EMAIL_TEXT;
                }

                User user = new User(userID, userFullName, email, bio, followers, following, repos, avatar_url, user_html_url);
                users.add(user);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the JSON results", e);
        }

        // Return the list of users
        return users;
    }

    public static List<User> fetchUserData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<User> users = extractFeatureFromJson(jsonResponse);

        // Return the {@link Event}
        return users;
    }
}