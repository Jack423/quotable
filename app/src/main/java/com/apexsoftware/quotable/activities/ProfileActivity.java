package com.apexsoftware.quotable.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.adapter.PostsByUserAdapter;
import com.apexsoftware.quotable.managers.listeners.OnObjectChangedListener;
import com.apexsoftware.quotable.models.Post;
import com.apexsoftware.quotable.models.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
    private TextView quoteCountTextView;
    private FloatingActionButton floatingActionButton;

    private String userID;
    private String currentUserId;
    FirebaseAuth firebaseAuth;

    private SwipeRefreshLayout swipeContainer;
    private PostsByUserAdapter postAdapter;

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
        quoteCountTextView= findViewById(R.id.tv_quotes_counter);
        floatingActionButton = findViewById(R.id.profile_create_post);

        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshAction();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreatePostActivity();
            }
        });
    }

    private void onRefreshAction() {
        postAdapter.loadPosts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        //loadProfile();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case CreatePostActivity.CREATE_NEW_POST_REQUEST:
                    postAdapter.loadPosts();
                    showSnackBar(R.string.message_post_was_created);
                    setResult(RESULT_OK);
                    break;
            }
        }
    }

    private void loadPostsList() {
        if (recyclerView == null) {
            recyclerView = findViewById(R.id.profile_list);
            postAdapter = new PostsByUserAdapter(this, userID);
            postAdapter.setCallBack(new PostsByUserAdapter.CallBack() {
                @Override
                public void onItemClick(Post post, View view) {

                }

                @Override
                public void onPostsListChanged(int quotesCount) {
                    quoteCountTextView.setText(buildCounterSpannable(quotesCount));

                    if (quotesCount > 0) {
                        quoteCountTextView.setText(0);
                    }

                    swipeContainer.setRefreshing(false);
                    hideLoadingPostsProgressBar();
                }

                @Override
                public void onPostLoadingCanceled() {
                    swipeContainer.setRefreshing(false);
                    hideLoadingPostsProgressBar();
                }
            });

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            recyclerView.setAdapter(postAdapter);
            postAdapter.loadPosts();
        }
    }

    private Spannable buildCounterSpannable(int value) {
        SpannableStringBuilder contentString = new SpannableStringBuilder();
        //contentString.append(String.valueOf(value));
        contentString.append("\n");
        int start = contentString.length();
        //contentString.append(label);
        contentString.setSpan(new TextAppearanceSpan(this, R.style.AppTheme_Text_Large), start, contentString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return contentString;
    }

    private void hideLoadingPostsProgressBar() {
        if (postsProgressBar.getVisibility() != View.GONE) {
            postsProgressBar.setVisibility(View.GONE);
        }
    }

    private OnObjectChangedListener<User> createOnProfileChangedListener() {
        return new OnObjectChangedListener<User>() {
            @Override
            public void onObjectChanged(User obj) {
                fillUIFields(obj);
            }
        };
    }

    private void fillUIFields(User user) {
        if (user != null) {
            nameTextView.setText(user.getName());

            if (user.getPictureUrl() != null) {
                Glide.with(this)
                        .load(user.getPictureUrl())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                scheduleStartPostponedTransition(profilePhoto);
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                scheduleStartPostponedTransition(profilePhoto);
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(profilePhoto);
            } else {
                progressBar.setVisibility(View.GONE);
                profilePhoto.setImageResource(R.drawable.ic_stub);
            }
        }
    }

    private void scheduleStartPostponedTransition(final ImageView imageView) {
        imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                supportStartPostponedEnterTransition();
                return true;
            }
        });
    }

    private void openCreatePostActivity() {
        Intent intent = new Intent(this, CreatePostActivity.class);
        startActivityForResult(intent, CreatePostActivity.CREATE_NEW_POST_REQUEST);
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
