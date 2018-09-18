package com.apexsoftware.quotable.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apexsoftware.quotable.ApplicationHelper;
import com.apexsoftware.quotable.FriendsFragment;
import com.apexsoftware.quotable.adapter.PostsAdapter;
import com.apexsoftware.quotable.managers.ProfileManager;
import com.apexsoftware.quotable.managers.listeners.OnObjectChangedListener;
import com.apexsoftware.quotable.models.Post;
import com.apexsoftware.quotable.models.User;
import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.modules.GlideApp;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.data.model.Resource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.Calendar;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int CREATE_POST_REQUEST = 1;
    private static final int RC_SIGN_IN = 123;

    //Adapter and recycler view are member variables
    private PostsAdapter postsAdapter;
    private ProfileManager userManager;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private TextView name;
    private TextView bio;
    private ImageView profilePhoto;
    private FragmentManager fragmentManager;

    Fragment fragment = null;

    //Firebase references
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();

    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        userManager = ProfileManager.getInstance(this);
        ApplicationHelper.initDatabaseHelper(getApplication());

        Class fragmentClass = null;
        fragmentClass = FriendsFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        fragmentManager = getSupportFragmentManager();

        name = headerView.findViewById(R.id.tv_name);
        bio = headerView.findViewById(R.id.tv_bio);
        profilePhoto = headerView.findViewById(R.id.iv_profile_photo);

        initContentView();
    }

    public void onProfileClick(Post post, View v) {
        openProfileActivity(post.getUserId(), v);
    }

    public void showFloatButtonRelatedSnackBar(int stringId) {
        showSnackBar(floatingActionButton, stringId);
    }

    public void openProfileActivity(String userId, View view) {
        Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.USER_ID_EXTRA_KEY, userId);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && view != null) {

            View authorImageView = view.findViewById(R.id.iv_profile);

            /*ActivityOptions options = ActivityOptions.
                    makeSceneTransitionAnimation(MainActivity.this,
                            new android.util.Pair<>(authorImageView, getString(R.string.post_author_image_transition_name)));*/
            startActivityForResult(intent, UserProfileActivity.CREATE_POST_FROM_PROFILE_REQUEST);
        } else {
            startActivityForResult(intent, UserProfileActivity.CREATE_POST_FROM_PROFILE_REQUEST);
        }
    }

    private void initContentView() {
        Log.d(TAG, "---Firebase User ID: " + auth.getUid());
        if (firebaseUser != null && auth != null) {
            userManager.getProfileValue(MainActivity.this, auth.getUid(), new OnObjectChangedListener<User>() {
                @Override
                public void onObjectChanged(User obj) {
                    name.setText(obj.getName());
                    bio.setText(obj.getBio());

                    //Glide.with(MainActivity.this).load(obj.getPictureUrl()).into(profilePhoto);
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("/images/user_" + firebaseUser.getUid());
                    GlideApp.with(MainActivity.this).load(storageReference).centerCrop().into(profilePhoto);
                }
            });
        } else {
            Intent intent = new Intent(this, LoginActivity2.class);
            startActivity(intent);
            finish();
        }

        if(recyclerView == null) {
            floatingActionButton = findViewById(R.id.fab);

            if(floatingActionButton != null) {
                floatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(hasInternetConnection()) {
                            Intent intent = new Intent(MainActivity.this, CreatePostActivity.class);
                            startActivity(intent);
                        } else {
                            showFloatButtonRelatedSnackBar(R.string.no_internet_connection);
                        }
                    }
                });
            }

            final ProgressBar progressBar = findViewById(R.id.progressBar);
            SwipeRefreshLayout swipeContainer = findViewById(R.id.swipeContainer);
            recyclerView = findViewById(R.id.list);
            postsAdapter = new PostsAdapter(this, swipeContainer);
            postsAdapter.setCallback(new PostsAdapter.Callback() {
                @Override
                public void onItemClick(Post post, View view) {
                    //Implement singular post view later
                }

                @Override
                public void onListLoadingFinished() {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onAuthorClick(String authorId, View view) {
                    openProfileActivity(authorId, view);
                }

                @Override
                public void onCanceled(String message) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                }
            });

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            recyclerView.setAdapter(postsAdapter);
            postsAdapter.loadFirstPage();
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
            Intent intent = new Intent(MainActivity.this, LoginActivity3.class);
            startActivity(intent);
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
                        //post.setUserImagePath(userImage);
                        reference.child("quotes").child(post.getPostId()).setValue(post);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        } else if (requestCode == RC_SIGN_IN) {

        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_feed) {
            //Main Feed
        } else if (id == R.id.nav_friends) {
            startActivity(new Intent(this, FriendsActivity.class));
        } else if (id == R.id.nav_friend_requests) {
            startActivity(new Intent(this, FriendRequestsActivity.class));
        } else if (id == R.id.nav_likes) {
            //TODO Implement likes viewer
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        } else if (id == R.id.nav_report_bug) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
