package com.example.android.githubusersproject;

import java.io.Serializable;

public class User implements Serializable {

    private long mUserID;
    private String mName;
    private String mEmail;
    private String mBio;
    private int mFollowers;
    private int mFollowing;
    private int mRepos;
    private String mAvatarUrl;
    private String mUserUrl;

    public static final String EMPTY_EMAIL_TEXT = "Private email";

    /** Constructor **/
    public User(long userID, String name, String email, String bio, int followers, int following, int repos, String avatar_url, String user_url) {
        mUserID = userID;
        mName = name;
        mEmail = email;
        mBio = bio;
        mFollowers = followers;
        mFollowing = following;
        mRepos = repos;
        mAvatarUrl = avatar_url;
        mUserUrl = user_url;
    }

    /** Getters **/
    public long getUserID() { return mUserID; }
    public String getName() { return mName; }
    public String getEmail() { return mEmail; }
    public String getBio() { return mBio; }
    public int getFollowers() { return mFollowers; }
    public int getFollowing() { return mFollowing; }
    public int getRepos() { return mRepos; }
    public String getAvatarUrl() { return mAvatarUrl; }
    public String getUserUrl() { return mUserUrl; }
}
