package com.apexsoftware.quotable.activities;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.adapter.PostsAdapter;
import com.apexsoftware.quotable.models.Post;
import com.apexsoftware.quotable.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class MainActivityBackup extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int CREATE_POST_REQUEST = 1;

    //Adapter and recycler view are member variables
    PostsAdapter adapter;
    RecyclerView list;
    Context context;
    View view;
    FloatingActionButton fab;

    String userImage;

    //Firebase references
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreatePostActivity.class);
                startActivityForResult(intent, CREATE_POST_REQUEST);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //setup profile photo for posts
        //userImage = reference.child(firebaseUser.getUid()).child("pictureUrl").getKey();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //adapter = new PostsAdapter(this);

        list = findViewById(R.id.list);
        list.setHasFixedSize(true);
        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);

        final ProgressBar progressBar = findViewById(R.id.progressBar);

        //Setup listeners for new quotes
        reference.child("quotes").addChildEventListener(new com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String s) {
                Post post = dataSnapshot.getValue(Post.class);
                adapter.loadFirstPage();

                list.scrollToPosition(0);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                adapter.removeSelectedPost();
            }

            @Override
            public void onChildMoved(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void onProfileClick(Post post, View v) {
        openProfileActivity(post.getUserId(), v);
    }

    public void showFloatButtonRelatedSnackBar(int stringId) {
        showSnackBar(fab, stringId);
    }

    public void openProfileActivity(String userId, View view) {
        Intent intent = new Intent(MainActivityBackup.this, UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.USER_ID_EXTRA_KEY, userId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && view != null) {

            View authorImageView = view.findViewById(R.id.iv_profile);

            ActivityOptions options = ActivityOptions.
                    makeSceneTransitionAnimation(MainActivityBackup.this,
                            new android.util.Pair<>(authorImageView, getString(R.string.post_author_image_transition_name)));
            startActivityForResult(intent, UserProfileActivity.CREATE_POST_FROM_PROFILE_REQUEST, options.toBundle());
        } else {
            startActivityForResult(intent, UserProfileActivity.CREATE_POST_FROM_PROFILE_REQUEST);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            auth.signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                        Post post = new Post(name, firebaseUser.getUid(), postText);
                        post.setUserImagePath(userImage);
                        reference.child("quotes").child(post.getPostId()).setValue(post);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_feed) {
            // Handle the camera action
        } else if (id == R.id.nav_friends) {

        } else if (id == R.id.nav_bookmarks) {

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_report_bug) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
