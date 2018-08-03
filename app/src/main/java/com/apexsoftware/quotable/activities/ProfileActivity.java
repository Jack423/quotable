package com.apexsoftware.quotable.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apexsoftware.quotable.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.apexsoftware.quotable.activities.UserProfileActivity.USER_ID_EXTRA_KEY;

public class ProfileActivity extends BaseActivity {

    //UI references
    private TextView nameTextView;
    private ImageView profilePhoto;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ProgressBar postsProgressBar;
    private SwipeRefreshLayout swipeContainer;

    private String userID;
    private String currentUserId;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        userID = getIntent().getStringExtra(USER_ID_EXTRA_KEY);
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();
        }

        nameTextView = findViewById(R.id.tv_profile_name);
        profilePhoto = findViewById(R.id.iv_profile_photo);
        recyclerView = findViewById(R.id.profile_list);
        progressBar = findViewById(R.id.progressBar);
        postsProgressBar = findViewById(R.id.postsProgressBar);

        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshAction();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_block) {
            return true;
        } else if (id == R.id.action_report) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
