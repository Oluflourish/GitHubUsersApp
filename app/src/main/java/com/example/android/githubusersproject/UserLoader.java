package com.example.android.githubusersproject;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Flourish on 03/08/2017.
 */
public class UserLoader extends AsyncTaskLoader<List<User>> {
    /** Tag for log messages **/
    private static final String LOG_TAG = UserLoader.class.getName();

    /** Query URL */
    private String mUrl;

    /**
     * Constructs a  new {@link UserLoader}.
     * @param context of the activity
     * @param url to load data from
     **/
    public UserLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /** This is no a background thread. */
    @Override
    public List<User> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of users
        List<User> users = QueryUtils.fetchUserData(mUrl);
        return users;
    }
}
