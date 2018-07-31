package com.apexsoftware.quotable.activities;
//Created by Jack Butler on 7/30/2018.

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.adapter.PostAdapterBackup;
import com.apexsoftware.quotable.models.Post;
import com.apexsoftware.quotable.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Calendar;

public class UserProfileActivity extends BaseActivity{
    private static final String TAG = UserProfileActivity.class.getSimpleName();
    private static final int CREATE_POST_REQUEST = 1;
    public static final String USER_ID_EXTRA_KEY = "UserProfileActivity.USER_ID_EXTRA_KEY";
    public static final int CREATE_POST_FROM_PROFILE_REQUEST = 22;

    PostAdapterBackup adapter;
    RecyclerView list;
    Context context;

    TextView name, userDescription, quotes, followers, following;
    Button follow;
    ImageView profilePicture;
    FloatingActionButton fab;

    //Firebase shite
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = auth.getCurrentUser();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_user_profile);

        name = findViewById(R.id.text_name);
        userDescription = findViewById(R.id.text_user_details);
        quotes = findViewById(R.id.text_quotes);
        followers = findViewById(R.id.text_followers);
        following = findViewById(R.id.text_following);
        follow = findViewById(R.id.btnFollow);
        fab = findViewById(R.id.btnCreate);
        list = findViewById(R.id.profile_list);
        profilePicture = findViewById(R.id.image_user_profile);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new PostAdapterBackup();

        list.setHasFixedSize(true);
        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);

        //Setup listeners for new quotes
        reference.child("quotes").addChildEventListener(new com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String s) {
                Post post = dataSnapshot.getValue(Post.class);
                adapter.addPost(post, 0);
                list.scrollToPosition(0);
            }

            @Override
            public void onChildChanged(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                adapter.removePost(post);
            }

            @Override
            public void onChildMoved(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(resultCode, resultCode, data);

        if(requestCode == CREATE_POST_REQUEST) {
            if(resultCode == RESULT_OK) {
                final Calendar c = Calendar.getInstance();
                final String postText = data.getStringExtra(CreatePostActivity.TEXT);
//                String user = getUserAccount();

                //Get the prefs we made earlier
                SharedPreferences prefs = getSharedPreferences("Quotable", MODE_PRIVATE);
                //Get our UUID from the store
                String uuid = prefs.getString("uuid", "");

                reference.child("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                        //Fill in our model
                        User user = dataSnapshot.getValue(User.class);
                        String name = user.getName();

                        //Create our yak with the user data
                        Post post = new Post(name, firebaseUser.getUid(), postText, c.getTimeInMillis());
                        reference.child("quotes").child(post.getPostId()).setValue(post);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }
}
