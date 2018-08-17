package com.apexsoftware.quotable.managers;
// Created by Jack Butler on 8/3/2018.

import android.content.Context;

import com.apexsoftware.quotable.ApplicationHelper;
import com.apexsoftware.quotable.managers.listeners.OnObjectChangedListener;
import com.apexsoftware.quotable.models.User;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserManager extends FirebaseListenersManager{
    private static final String TAG = UserManager.class.getSimpleName();
    private static UserManager instance;

    private Context context;
    private DatabaseHelper databaseHelper;
    private Map<Context, List<ValueEventListener>> activeListeners = new HashMap<>();

    public static UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context);
        }

        return instance;
    }

    private UserManager(Context context) {
        this.context = context;
        databaseHelper = ApplicationHelper.getDatabaseHelper();
    }

    public void getProfileValue(Context activityContext, String id, final OnObjectChangedListener<User> listener) {
        ValueEventListener valueEventListener = databaseHelper.getProfile(id, listener);
        addListenerToMap(activityContext, valueEventListener);
    }

    public void getProfileSingleValue(String id, final OnObjectChangedListener<User> listener) {
        databaseHelper.getProfileSingleValue(id, listener);
    }

    public void incrementPostCount(User user) {
        int val = user.getPostCount() + 1;
        databaseHelper.updatePostCount(user, val);
    }

    public void incrementFollowingCount(User user) {
        int val = user.getFollowing() + 1;
        databaseHelper.updateFollowingCount(user, val);
    }

    public void incrementFollowersCount(User user) {
        int val = user.getFollowers() + 1;
        databaseHelper.updateFollowerCount(user, val);
    }
}
