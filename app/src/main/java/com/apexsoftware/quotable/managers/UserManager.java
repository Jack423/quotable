package com.apexsoftware.quotable.managers;
// Created by Jack Butler on 8/3/2018.

import android.content.Context;

import com.apexsoftware.quotable.ApplicationHelper;
import com.apexsoftware.quotable.enums.ProfileStatus;
import com.apexsoftware.quotable.managers.listeners.OnObjectChangedListener;
import com.apexsoftware.quotable.models.User;
import com.apexsoftware.quotable.util.PreferencesUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    public ProfileStatus checkProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            return ProfileStatus.NOT_AUTHORIZED;
        } else if (!PreferencesUtil.isProfileCreated(context)){
            return ProfileStatus.NO_PROFILE;
        } else {
            return ProfileStatus.PROFILE_CREATED;
        }
    }
}
