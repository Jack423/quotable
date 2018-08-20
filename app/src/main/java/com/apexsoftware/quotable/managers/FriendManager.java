package com.apexsoftware.quotable.managers;
//Created by Jack Butler on 8/20/2018.

import android.content.Context;

import com.apexsoftware.quotable.ApplicationHelper;
import com.apexsoftware.quotable.managers.listeners.OnFriendChangedListener;

public class FriendManager extends FirebaseListenersManager {
    private static final String TAG = FriendManager.class.getSimpleName();

    public String CURRENT_STATE;

    private Context context;
    private static FriendManager instance;

    public static FriendManager getInstance(Context context) {
        if(instance == null) {
            instance = new FriendManager(context);
        }

        return instance;
    }

    private FriendManager(Context context) {
        this.context = context;
    }

    public void addFriend(String friendId, String currentState) {
        DatabaseHelper databaseHelper = ApplicationHelper.getDatabaseHelper();
        databaseHelper.addFollower(friendId, currentState);
    }

    public void getFriendList() {

    }

    /*public void isFriendAdded(String friendId, final) {

    }*/
}
