package com.apexsoftware.quotable.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.adapter.RequestsAdapter;
import com.apexsoftware.quotable.models.Friend;
import com.apexsoftware.quotable.models.FriendRequest;
import com.apexsoftware.quotable.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestsActivity extends AppCompatActivity {
    public static final String TAG = FriendRequestsActivity.class.getSimpleName();

    List<FriendRequest> requestList = new ArrayList<>();

    RecyclerView recyclerView;
    Toolbar toolbar;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);

        toolbar = findViewById(R.id.friend_request_toolbar);

        toolbar.setTitle("Friend Requests");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerView = findViewById(R.id.friend_request_list);

        RequestsAdapter requestsAdapter = new RequestsAdapter(requestList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(requestsAdapter);

        initView();
        Log.d(TAG, "---INITIALIZED VIEW---");
    }

    private void initView() {
        databaseReference.child("Friend_req").child(firebaseUser.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final FriendRequest friendRequest = dataSnapshot.getValue(FriendRequest.class);

                String nameTest = friendRequest.getName();

                assert friendRequest != null;
                if (friendRequest.getRequestType().equals("received")) {
                    databaseReference.child("users").child(friendRequest.getId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            friendRequest.setName(user.getName());
                            friendRequest.setBio(user.getBio());
                            friendRequest.setProfilePhotoUrl(user.getPictureUrl());
                            requestList.add(friendRequest);
                            Log.d(TAG, "----ADDED REQUEST TO LIST----" + friendRequest.getName());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
