package com.example.android.githubusersproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class UserDetailActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "prefs";
    public static final String PREFS_DARK_THEME = "dark_theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Use the chosen theme
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean useDarkTheme = preferences.getBoolean(PREFS_DARK_THEME, false);
        if (useDarkTheme) {
            setTheme(R.style.AppTheme_Dark);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_detail_activity);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        // Get the user object data from the intent which will be loaded into the views
        User user = (User) getIntent().getSerializableExtra(UserListActivity.USER_DETAIL_KEY);
        loadUser(user);

        // Set an onClickListener on the user_url link and the moreButton
        final TextView tvUrl = (TextView) findViewById(R.id.user_url);
        final Button moreButton = (Button) findViewById(R.id.more_button);
        final String userUrl = String.valueOf(tvUrl.getText());

        tvUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send an implicit intent to open url in browser
                openLink(userUrl);
            }
        });

        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send an implicit intent to open url in browser
                openLink(userUrl);
            }
        });

    }

    private void openLink(String user_url) {
        Uri userUri = Uri.parse(user_url);
        Intent intent = new Intent(Intent.ACTION_VIEW, userUri);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            setShareIntent();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // TODO: Optimize the use of textView by making them private and global

    private void loadUser(User user) {
        // Find the corresponding views
        TextView tvName = (TextView) findViewById(R.id.user_name);
        TextView tvEmail = (TextView) findViewById(R.id.user_email);
        TextView tvBio = (TextView) findViewById(R.id.user_bio);
        TextView tvFollowers = (TextView) findViewById(R.id.user_followers);
        TextView tvFollowing = (TextView) findViewById(R.id.user_following);
        TextView tvRepos = (TextView) findViewById(R.id.user_repos);
        TextView tvUrl = (TextView) findViewById(R.id.user_url);
        ImageView imageView = (ImageView) findViewById(R.id.user_image);


        // Display corresponding data into the TextViews
        tvName.setText(user.getName());
        tvFollowers.setText(String.valueOf(user.getFollowers()));
        tvFollowing.setText(String.valueOf(user.getFollowing()));
        tvRepos.setText(String.valueOf(user.getRepos()));
        tvUrl.setText(String.valueOf(user.getUserUrl()));

        // Hide email textView if null/empty otherwise setText
        String email = user.getEmail();
        String email_empty = User.EMPTY_EMAIL_TEXT;
        if (TextUtils.isEmpty(email) || email.equals(email_empty)) {
            tvEmail.setVisibility(View.GONE);
        } else {
            tvEmail.setText(email);
        }

        // Hide bio textView if null/empty otherwise setText
        String bio = user.getBio();
        if (TextUtils.isEmpty(bio) || bio.equals("null")) {
            tvBio.setVisibility(View.GONE);
        } else {
            tvBio.setText(bio);
        }

        // TODO: Put a circle border
        // Find the TextView with view ID location
        //Picasso.with(getContext()).load(Uri.parse(currentUser.getAvatarUrl())).error(R.mipmap.ic_user).resize(100,100).into(imageView);
        Picasso.with(this)
                .load(Uri.parse(user.getAvatarUrl()))
                //.error(R.mipmap.ic_user)
                .error(R.drawable.github_splash)
                .transform(new CircleTransform())
                .into(imageView);
    }

    private void setShareIntent() {
        // Get the user_name and user_url from the text views
        TextView tvName = (TextView) findViewById(R.id.user_name);
        TextView tvUrl = (TextView) findViewById(R.id.user_url);

        ImageView imageView = (ImageView) findViewById(R.id.user_image);

        String share_message = "Check out this awesome developer @" + tvName.getText() + ", " +
                                                                        tvUrl.getText() + ".";

        // Get access to the URI for the bitmap
        Uri bmpUri = getLocalBitmapUri(imageView);
        // Construct a ShareIntent with link to image
        Intent shareIntent = new Intent();
        // Construct a ShareIntent with link to image
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("*/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, share_message);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        // Launch share menu
        startActivity(Intent.createChooser(shareIntent, "Share Developer"));
    }

    // Returns the URI path to the Bitmap of user_image
    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file =  new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

}
