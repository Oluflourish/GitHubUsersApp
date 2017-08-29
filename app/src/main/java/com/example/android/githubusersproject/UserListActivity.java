/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.githubusersproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity implements LoaderCallbacks<List<User>> {

    /** URL for user data from the GitHub URL */
    private static final String BASE_URL = "https://api.github.com/";

    /** Adapter for the list of users **/
    private UserAdapter mAdapter;

    /** Constant value for the user loader ID */
    private static final int USER_LOADER_ID = 1;

    /** TextView that is displayed when the list is empty **/
    private TextView mEmptyStateTextView;
    private View mEmptyStateView;
    private Button retryButton;


    public static final String USER_DETAIL_KEY = "user";

    public static final String PREFS_NAME = "prefs";
    public static final String PREFS_DARK_THEME = "dark_theme";
    boolean useDarkTheme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Use the chosen theme
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        useDarkTheme = preferences.getBoolean(PREFS_DARK_THEME, false);
        if (useDarkTheme) {
            setTheme(R.style.AppTheme_Dark);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list_activity);


        // Find a reference to the {@link ListView} in the layout
        ListView userListView = (ListView) findViewById(R.id.list);

//        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view_text);
//        userListView.setEmptyView(mEmptyStateTextView);

        mEmptyStateView = findViewById(R.id.empty_view);
        userListView.setEmptyView(mEmptyStateView);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view_text);
        retryButton = (Button) findViewById(R.id.empty_view_retry);

        // Hide the empty state views
        mEmptyStateView.setVisibility(View.GONE);


        // Create a new {@link ArrayAdapter} of users
        mAdapter = new UserAdapter(this, new ArrayList<User>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        userListView.setAdapter(mAdapter);


        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Find the current user that was clicked on
                User currentUser = mAdapter.getItem(position);

                // Create a new intent and add the user URL as an extract data
                Intent intent = new Intent(UserListActivity.this, UserDetailActivity.class);
                intent.putExtra(USER_DETAIL_KEY, currentUser);

                // Send the intent to launch a new activity
                startActivity(intent);
            }
        });

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUsers();
            }
        });

        //TODO: Create a loadUser() method so that the activity can be refreshed when there is no internet connection
        loadUsers();
    }

    private void loadUsers() {
        View loadingSpinner = findViewById(R.id.loading_spinner);

        // Hide the empty state views
        mEmptyStateView.setVisibility(View.GONE);
        // Show loadingSpinner
        loadingSpinner.setVisibility(View.VISIBLE);

        // Get a reference to the ConnectivityManager to check the state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        //If there is a connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            //if (networkInfo == null) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant define above and pass in null for
            // the bundle. pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(USER_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide the loading indicator so error message will be visible
            loadingSpinner.setVisibility(View.GONE);

            // Update empty state with no internet connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);

            // Show empty state views
            mEmptyStateView.setVisibility(View.VISIBLE);
        }
    }


    private void toggleTheme(boolean darkTheme) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(PREFS_DARK_THEME, darkTheme);
        editor.apply();

        // Restart current activity
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.theme_toggle);
        item.setActionView(R.layout.switch_layout);

        SwitchCompat toggle = (SwitchCompat) item.getActionView().findViewById(R.id.switch_for_action_bar);
        toggle.setChecked(useDarkTheme);
        if (toggle != null) {
            toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    toggleTheme(isChecked);
                }
            });
        }

        return true;
    }

    @Override
    public Loader<List<User>> onCreateLoader(int i, Bundle bundle) {

        String url = BASE_URL +
                "search/users?q=language:java+location:lagos";
                //"?access_token=515f1006bbb85ac3845c547758a92723877be461";
        // Create a new loader for the given URL
        return new UserLoader(this, url);
    }

    @Override
    public void onLoadFinished(Loader<List<User>> loader, List<User> users) {
        // Hide loading spinner because the data has been loaded
        View loadingSpinner = findViewById(R.id.loading_spinner);
        loadingSpinner.setVisibility(View.GONE);

        // Set empty state text to display "No users found."
        mEmptyStateTextView.setText(R.string.no_users);

        // Show empty state views
        mEmptyStateView.setVisibility(View.VISIBLE);


        // Clear the adaptor of previous user data
        mAdapter.clear();

        if (users != null && !users.isEmpty()) {
            mAdapter.addAll(users);
        }
    }


    @Override
    public void onLoaderReset(Loader<List<User>> loader) {
        // Loader reset, so we can clear out our existing data for a new one.
        mAdapter.clear();
    }
}
