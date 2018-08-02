package com.apexsoftware.quotable.managers;
//Created by Jack Butler on 8/1/2018.

import android.content.Context;
import android.util.Log;

import com.apexsoftware.quotable.ApplicationHelper;
import com.apexsoftware.quotable.managers.listeners.OnPostListChangedListener;
import com.apexsoftware.quotable.models.Post;

public class PostManager extends FirebaseListenersManager{
    private static final String TAG = PostManager.class.getSimpleName();
    private static PostManager instance;
    //private int newPostsCounter = 0;

    private Context context;

    public static PostManager getInstance(Context context) {
        if (instance == null) {
            instance = new PostManager(context);
        }

        return instance;
    }

    private PostManager(Context context) {
        this.context = context;
    }

    public void getPostsList(OnPostListChangedListener<Post> onDataChangedListener, long date) {
        ApplicationHelper.getDatabaseHelper().getPostList(onDataChangedListener, date);
    }
}
