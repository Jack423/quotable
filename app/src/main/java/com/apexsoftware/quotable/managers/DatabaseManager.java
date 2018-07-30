package com.apexsoftware.quotable.managers;
//Created by Jack Butler on 7/25/2018.

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.apexsoftware.quotable.Constants;
import com.apexsoftware.quotable.adapter.PostListResult;
import com.apexsoftware.quotable.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager {
    public static final String TAG = DatabaseManager.class.getSimpleName();

    private static DatabaseManager instance;

    private Context context;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseStorage storage;
    FirebaseAuth firebaseAuth;

    public static DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }

        return instance;
    }

    public DatabaseManager(Context context) {
        this.context = context;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void init() {
        storage = FirebaseStorage.getInstance();
        storage.setMaxUploadRetryTimeMillis(60000);
    }

    public DatabaseReference getDatabaseReference() {
        return database.getReference();
    }

    public void createOrUpdatePost(Post post) {
        try {
            DatabaseReference reference = database.getReference();

            Map<String, Object> postValues = post.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/posts/" + post.getPostId(), postValues);

            reference.updateChildren(childUpdates);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void getPostsByUser(String userId) {
        DatabaseReference reference = database.getReference("posts");
        Query query;
        query = reference.orderByChild("authorId").equalTo(userId);

        query.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PostListResult result = parsePostList((Map<String, Object>) dataSnapshot.getValue());
                result.getPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "getPostsByUser(), onCanceled", new Exception(databaseError.getMessage()));
            }
        });
    }

    public PostListResult parsePostList(Map<String, Object> objectMap) {
        PostListResult result = new PostListResult();
        List<Post> list = new ArrayList<Post>();
        boolean isMoreDataAvailable = true;
        long lastItemCreatedDate = 0;

        if (objectMap != null) {
            isMoreDataAvailable = Constants.Post.POST_AMOUNT_ON_PAGE == objectMap.size();

            for (String key : objectMap.keySet()) {
                Object obj = objectMap.get(key);
                if (obj instanceof Map) {
                    Map<String, Object> mapObj = (Map<String, Object>) obj;

                    /*if(!isPostValid(mapObj)) {
                        Log.d(TAG, "Invalid post, id: " + key);
                        continue;
                    }*/

                    //boolean hasComplain = mapObj.containsKey("hasComplain") && (boolean) mapObj.get("hasComplain");
                    long createdDate = (long) mapObj.get("createdDate");

                    if (lastItemCreatedDate == 0 || lastItemCreatedDate > createdDate) {
                        lastItemCreatedDate = createdDate;
                    }

                    Post post = new Post();
                    post.setPostId(key);
                    post.setUser((String) mapObj.get("user"));
                    post.setText((String) mapObj.get("text"));
                    post.setUserId((String) mapObj.get("authorId"));
                    post.setCreatedAt(createdDate);
                    /*
                    if (mapObj.containsKey("commentsCount")) {
                        post.setCommentsCount((long) mapObj.get("commentsCount"));
                    }
                    if (mapObj.containsKey("likesCount")) {
                        post.setBookmarksCount((long) mapObj.get("bookmarksCount"));
                    }
                    if (map.containsKey("viewsCount")) {
                        post.setViewsCount((long) mapObj.get("viewsCount"));
                    }
                    */
                    list.add(post);
                }
            }

            Collections.sort(list, new Comparator<Post>() {
                @Override
                public int compare(Post lhs, Post rhs) {
                    return 0;
                }
            });

            result.setPosts(list);
            result.setLastItemCreatedDate(lastItemCreatedDate);
            result.setMoreDataAvailable(isMoreDataAvailable);
        }

        return result;
    }
}
