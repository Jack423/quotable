package com.apexsoftware.quotable.activities.managers;
//Created by Jack Butler on 7/25/2018.

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.apexsoftware.quotable.adapter.PostListResult;
import com.apexsoftware.quotable.models.DataPost;
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
        if(instance == null) {
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

    public void createOrUpdatePost(DataPost post) {
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
                PostListResult result = parsePostList
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public PostListResult parsePostList(Map<String, Object> objectMap) {
        PostListResult result = new PostListResult();
        List<DataPost> list = new ArrayList<>()
    }
}
