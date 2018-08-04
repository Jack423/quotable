package com.apexsoftware.quotable.managers;
//Created by Jack Butler on 8/1/2018.

import android.content.Context;
import android.util.Log;

import com.apexsoftware.quotable.Constants;
import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.managers.listeners.OnDataChangedListener;
import com.apexsoftware.quotable.managers.listeners.OnPostListChangedListener;
import com.apexsoftware.quotable.models.Post;
import com.apexsoftware.quotable.models.PostListResult;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper {
    public static final String TAG = DatabaseHelper.class.getSimpleName();

    private static DatabaseHelper instance;

    private Context context;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private Map<ValueEventListener, DatabaseReference> activeListeners = new HashMap<>();

    public static DatabaseHelper getInstance(Context context) {
        if(instance == null) {
            instance = new DatabaseHelper(context);
        }

        return instance;
    }

    public DatabaseHelper(Context context) {
        this.context = context;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void init() {
        database = FirebaseDatabase.getInstance();
        //database.setPersistenceEnabled(true);
        storage = FirebaseStorage.getInstance();
    }

    public DatabaseReference getDatabaseReference() {
        return database.getReference();
    }

    public void closeListener(com.google.firebase.database.ValueEventListener listener) {
        if (activeListeners.containsKey(listener)) {
            DatabaseReference reference = activeListeners.get(listener);
            reference.removeEventListener(listener);
            activeListeners.remove(listener);
            Log.d(TAG, "closeListener(), listener was removed: " + listener);
        } else {
            Log.d(TAG, "closeListener(), listener not found :" + listener);
        }
    }

    public void getPostList(final OnPostListChangedListener<Post> onDataChangedListener, long date) {
        DatabaseReference databaseReference = database.getReference("quotes");
        Query postsQuery;
        if (date == 0) {
            postsQuery = databaseReference.limitToLast(Constants.Post.POST_AMOUNT_ON_PAGE).orderByChild("createdAt");
        } else {
            postsQuery = databaseReference.limitToLast(Constants.Post.POST_AMOUNT_ON_PAGE).endAt(date).orderByChild("createdAt");
        }

        postsQuery.keepSynced(true);
        postsQuery.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> objectMap = (Map<String, Object>) dataSnapshot.getValue();
                PostListResult result = parsePostList(objectMap);

                if (result.getPosts().isEmpty() && result.isMoreDataAvailable()) {
                    getPostList(onDataChangedListener, result.getLastItemCreatedDate() - 1);
                } else {
                    onDataChangedListener.onListChanged(parsePostList(objectMap));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "getPostList(), onCancelled", new Exception(databaseError.getMessage()));
                onDataChangedListener.onCanceled(context.getString(R.string.permission_denied_error));
            }
        });
    }

    private PostListResult parsePostList(Map<String, Object> objectMap) {
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

                    if (!isPostValid(mapObj)) {
                        Log.d(TAG, "Invalid post, id: " + key);
                        continue;
                    }

                    boolean hasComplain = mapObj.containsKey("hasComplain") && (boolean) mapObj.get("hasComplain");
                    long createdDate = (long) mapObj.get("createdAt");

                    if (lastItemCreatedDate == 0 || lastItemCreatedDate > createdDate) {
                        lastItemCreatedDate = createdDate;
                    }

                    if (!hasComplain) {
                        /*Post post = new Post();
                        post.setId(key);
                        post.setTitle((String) mapObj.get("title"));
                        post.setDescription((String) mapObj.get("description"));
                        //post.setImagePath((String) mapObj.get("imagePath"));
                        post.setImageTitle((String) mapObj.get("imageTitle"));
                        post.setAuthorId((String) mapObj.get("authorId"));
                        post.setCreatedDate(createdDate);
                        if (mapObj.containsKey("commentsCount")) {
                            post.setCommentsCount((long) mapObj.get("commentsCount"));
                        }
                        if (mapObj.containsKey("likesCount")) {
                            post.setLikesCount((long) mapObj.get("likesCount"));
                        }
                        if (mapObj.containsKey("watchersCount")) {
                            post.setWatchersCount((long) mapObj.get("watchersCount"));
                        }
                        list.add(post);*/

                        Post post = new Post();
                        post.setPostId(key);
                        post.setUser((String) mapObj.get("user"));
                        post.setText((String) mapObj.get("text"));
                        post.setCreatedAt(createdDate);
                        post.setUserId((String) mapObj.get(("userId")));

                        list.add(post);
                    }
                }
            }

            Collections.sort(list, new Comparator<Post>() {
                @Override
                public int compare(Post lhs, Post rhs) {
                    return ((Long) rhs.getCreatedAt()).compareTo(lhs.getCreatedAt());
                }
            });

            result.setPosts(list);
            result.setLastItemCreatedDate(lastItemCreatedDate);
            result.setMoreDataAvailable(isMoreDataAvailable);
        }

        return result;
    }

    public void getPostListByUser(final OnDataChangedListener<Post> onDataChangedListener, String userId) {
        DatabaseReference databaseReference = database.getReference("posts");
        Query postsQuery;
        postsQuery = databaseReference.orderByChild("userId").equalTo(userId);

        postsQuery.keepSynced(true);
        postsQuery.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PostListResult result = parsePostList((Map<String, Object>) dataSnapshot.getValue());
                onDataChangedListener.onListChanged(result.getPosts());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "getPostListByUser(), onCancelled", new Exception(databaseError.getMessage()));
            }
        });
    }


    private boolean isPostValid(Map<String, Object> post) {
        return post.containsKey("postId")
                && post.containsKey("createdAt")
                && post.containsKey("text")
                && post.containsKey("user")
                && post.containsKey("userId");
    }
}
