package com.apexsoftware.quotable.managers;
//Created by Jack Butler on 8/1/2018.

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.apexsoftware.quotable.ApplicationHelper;
import com.apexsoftware.quotable.managers.listeners.OnDataChangedListener;
import com.apexsoftware.quotable.managers.listeners.OnObjectExistListener;
import com.apexsoftware.quotable.managers.listeners.OnPostCreatedListener;
import com.apexsoftware.quotable.managers.listeners.OnPostListChangedListener;
import com.apexsoftware.quotable.models.Like;
import com.apexsoftware.quotable.models.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

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

    public void createOrUpdatePost(final OnPostCreatedListener onPostCreatedListener, Post post) {
        DatabaseHelper databaseHelper = ApplicationHelper.getDatabaseHelper();

        if (post.getPostId() == null) {
            post.setPostId(databaseHelper.generatePostId());
        }

        try {
            ApplicationHelper.getDatabaseHelper().createOrUpdatePost(post);
            onPostCreatedListener.onPostSaved(true);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void getPostsList(OnPostListChangedListener<Post> onDataChangedListener, long date) {
        ApplicationHelper.getDatabaseHelper().getPostList(onDataChangedListener, date);
    }

    public void getPostsListByUser(OnDataChangedListener<Post> onDataChangedListener, String userId) {
        ApplicationHelper.getDatabaseHelper().getPostListByUser(onDataChangedListener, userId);
    }

    public void isPostExistSingleValue(String postId, final OnObjectExistListener<Post> onObjectExistListener) {
        DatabaseHelper databaseHelper = ApplicationHelper.getDatabaseHelper();
        databaseHelper.isPostExistSingleValue(postId, onObjectExistListener);
    }

    public void hasCurrentUserLike(Context activityContext, String postId, String userId, final OnObjectExistListener<Like> onObjectExistListener) {
        DatabaseHelper databaseHelper = ApplicationHelper.getDatabaseHelper();
        ValueEventListener valueEventListener = databaseHelper.hasCurrentUserLike(postId, userId, onObjectExistListener);
        addListenerToMap(activityContext, valueEventListener);
    }

    public void hasCurrentUserLikeSingleValue(String postId, String userId, final OnObjectExistListener<Like> onObjectExistListener) {
        DatabaseHelper databaseHelper = ApplicationHelper.getDatabaseHelper();
        databaseHelper.hasCurrentUserLikeSingleValue(postId, userId, onObjectExistListener);
    }
}
