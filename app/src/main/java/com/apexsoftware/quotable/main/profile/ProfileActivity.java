package com.apexsoftware.quotable.main.profile;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.adapters.PostsByUserAdapter;
import com.apexsoftware.quotable.dialogs.UnfollowConfirmationDialog;
import com.apexsoftware.quotable.enums.FollowState;
import com.apexsoftware.quotable.main.base.BaseActivity;
import com.apexsoftware.quotable.main.editProfile.EditProfileActivity;
import com.apexsoftware.quotable.main.main.MainActivity;
import com.apexsoftware.quotable.main.post.createPost.CreatePostActivity;
import com.apexsoftware.quotable.main.postDetails.PostDetailsActivity;
import com.apexsoftware.quotable.main.userList.UsersListActivity;
import com.apexsoftware.quotable.main.userList.UsersListType;
import com.apexsoftware.quotable.model.Post;
import com.apexsoftware.quotable.model.Profile;
import com.apexsoftware.quotable.util.GlideApp;
import com.apexsoftware.quotable.util.ImageUtil;
import com.apexsoftware.quotable.util.LogUtil;
import com.apexsoftware.quotable.util.LogoutHelper;
import com.apexsoftware.quotable.views.FollowButton;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends BaseActivity<ProfileView, ProfilePresenter> implements ProfileView, GoogleApiClient.OnConnectionFailedListener, UnfollowConfirmationDialog.Callback {

    private static final String TAG = ProfileActivity.class.getSimpleName();
    public static final int CREATE_POST_FROM_PROFILE_REQUEST = 22;
    public static final String USER_ID_EXTRA_KEY = "ProfileActivity.USER_ID_EXTRA_KEY";

    // UI references.
    private TextView nameTextView;
    private TextView handleTextView;
    private TextView bioTextView;
    private TextView dateJoinedTextView;
    private ImageView imageView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView postsCounterTextView;
    private ProgressBar postsProgressBar;

    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private String currentUserId;
    private String userID;

    private PostsByUserAdapter postsAdapter;
    private SwipeRefreshLayout swipeContainer;
    private TextView likesCounterTextView;
    private TextView followersCounterTextView;
    private TextView followingsCounterTextView;
    private FollowButton followButton;
    private Button editProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_new);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        userID = getIntent().getStringExtra(USER_ID_EXTRA_KEY);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();
        }

        //Setup
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.imageView);
        nameTextView = findViewById(R.id.nameTextView);
        handleTextView = findViewById(R.id.handleTextView);
        bioTextView = findViewById(R.id.bioTextView);
        dateJoinedTextView = findViewById(R.id.dateJoinedTextView);

        postsCounterTextView = findViewById(R.id.postsCounterTextView);
        likesCounterTextView = findViewById(R.id.likesCounterTextView);
        followersCounterTextView = findViewById(R.id.followersCounterTextView);
        followingsCounterTextView = findViewById(R.id.followingsCounterTextView);
        postsProgressBar = findViewById(R.id.postsProgressBar);
        followButton = findViewById(R.id.followButton);
        swipeContainer = findViewById(R.id.swipeContainer);
        //editProfileButton = findViewById(R.id.editProfileButton);

        /*if (!currentUserId.equals(userID)) {
            editProfileButton.setVisibility(View.GONE);
        }*/

        initListeners();

        presenter.checkFollowState(userID);

        loadPostsList();
        supportPostponeEnterTransition();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.loadProfile(this, userID);
        presenter.getFollowersCount(userID);
        presenter.getFollowingsCount(userID);
    }

    @NonNull
    @Override
    public ProfilePresenter createPresenter() {
        if (presenter == null) {
            return new ProfilePresenter(this);
        }
        return presenter;
    }

    private void initListeners() {
        followButton.setOnClickListener(v -> {
            presenter.onFollowButtonClick(followButton.getState(), userID);
        });

        /*editProfileButton.setOnClickListener(v -> {
            presenter.onEditProfileButtonClick();
        });*/

        followingsCounterTextView.setOnClickListener(v -> {
            startUsersListActivity(UsersListType.FOLLOWINGS);
        });

        followersCounterTextView.setOnClickListener(v -> {
            startUsersListActivity(UsersListType.FOLLOWERS);
        });

        swipeContainer.setOnRefreshListener(this::onRefreshAction);
    }

    private void startUsersListActivity(int usersListType) {
        Intent intent = new Intent(ProfileActivity.this, UsersListActivity.class);
        intent.putExtra(UsersListActivity.USER_ID_EXTRA_KEY, userID);
        intent.putExtra(UsersListActivity.USER_LIST_TYPE, usersListType);
        startActivity(intent);
    }

    private void onRefreshAction() {
        postsAdapter.loadPosts();
    }

    private void loadPostsList() {
        if (recyclerView == null) {

            recyclerView = findViewById(R.id.recycler_view);
            postsAdapter = new PostsByUserAdapter(this, userID);
            postsAdapter.setCallBack(new PostsByUserAdapter.CallBack() {
                @Override
                public void onItemClick(final Post post, final View view) {
                    presenter.onPostClick(post, view);
                }

                @Override
                public void onPostsListChanged(int postsCount) {
                    presenter.onPostListChanged(postsCount);
                }

                @Override
                public void onPostLoadingCanceled() {
                    hideLoadingPostsProgress();
                }
            });

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            recyclerView.setAdapter(postsAdapter);
            postsAdapter.loadPosts();
        }
    }

    @Override
    public void showUnfollowConfirmation(Profile profile) {
        UnfollowConfirmationDialog unfollowConfirmationDialog = new UnfollowConfirmationDialog();
        Bundle args = new Bundle();
        args.putSerializable(UnfollowConfirmationDialog.PROFILE, profile);
        unfollowConfirmationDialog.setArguments(args);
        unfollowConfirmationDialog.show(getFragmentManager(), UnfollowConfirmationDialog.TAG);
    }

    @Override
    public void updateFollowButtonState(FollowState followState) {
        followButton.setState(followState);
    }

    @Override
    public void setFollowStateChangeResultOk() {
        setResult(UsersListActivity.UPDATE_FOLLOWING_STATE_RESULT_OK);
    }

    @Override
    public void startEditProfileActivity() {
        Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void setProfileName(String username) {
        nameTextView.setText(username);
    }

    @Override
    public void setProfileHandle(String handle) {
        handleTextView.setText(handle);
    }

    @Override
    public void setProfileBio(String bio) {
        bioTextView.setText(bio);
    }

    @Override
    public void setProfileDateJoined(String dateJoined) {
        dateJoinedTextView.setText(dateJoined);
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

    private void startMainActivity() {
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void setProfilePhoto(String photoUrl) {
        ImageUtil.loadImage(GlideApp.with(this), photoUrl, imageView, new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                scheduleStartPostponedTransition(imageView);
                progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                scheduleStartPostponedTransition(imageView);
                progressBar.setVisibility(View.GONE);
                return false;
            }
        });
    }

    @Override
    public void setDefaultProfilePhoto() {
        progressBar.setVisibility(View.GONE);
        imageView.setImageResource(R.drawable.ic_stub);
    }

    @Override
    public void updateFollowersCount(int count) {
        followersCounterTextView.setVisibility(View.VISIBLE);
        String followersLabel = getResources().getQuantityString(R.plurals.followers_counter_format, count, count);
        followersCounterTextView.setText(presenter.buildCounterSpannable(followersLabel, count));
    }

    @Override
    public void updateFollowingsCount(int count) {
        followingsCounterTextView.setVisibility(View.VISIBLE);
        String followingsLabel = getResources().getQuantityString(R.plurals.followings_counter_format, count, count);
        followingsCounterTextView.setText(presenter.buildCounterSpannable(followingsLabel, count));
    }

    @Override
    public void openPostDetailsActivity(Post post, View postItemView) {
        Intent intent = new Intent(ProfileActivity.this, PostDetailsActivity.class);
        intent.putExtra(PostDetailsActivity.POST_ID_EXTRA_KEY, post.getId());
        intent.putExtra(PostDetailsActivity.AUTHOR_ANIMATION_NEEDED_EXTRA_KEY, true);

        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.
                    makeSceneTransitionAnimation(ProfileActivity.this,
                            new android.util.Pair<>(imageView, getString(R.string.post_image_transition_name))
                    );
            startActivityForResult(intent, PostDetailsActivity.UPDATE_POST_REQUEST, options.toBundle());
        } else {
            startActivityForResult(intent, PostDetailsActivity.UPDATE_POST_REQUEST);
        }*/

        startActivityForResult(intent, PostDetailsActivity.UPDATE_POST_REQUEST);
    }

    @Override
    public void openCreatePostActivity() {
        Intent intent = new Intent(this, CreatePostActivity.class);
        startActivityForResult(intent, CreatePostActivity.CREATE_NEW_POST_REQUEST);
    }

    @Override
    public void updateLikesCounter(Spannable text) {
        likesCounterTextView.setText(text);
    }

    @Override
    public void hideLoadingPostsProgress() {
        swipeContainer.setRefreshing(false);
        if (postsProgressBar.getVisibility() != View.GONE) {
            postsProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void showLikeCounter(boolean show) {
        likesCounterTextView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updatePostsCounter(Spannable text) {
        postsCounterTextView.setText(text);
    }

    @Override
    public void showPostCounter(boolean show) {
        postsCounterTextView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onPostRemoved() {
        postsAdapter.removeSelectedPost();
    }

    @Override
    public void onPostUpdated() {
        postsAdapter.updateSelectedPost();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (userID.equals(currentUserId)) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.profile_menu, menu);
            return true;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.editProfile:
                presenter.onEditProfileClick();
                return true;
            case R.id.signOut:
                LogoutHelper.signOut(mGoogleApiClient, this);
                startMainActivity();
                return true;
            case R.id.createPost:
                presenter.onCreatePostClick();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onUnfollowButtonClicked() {
        presenter.unfollowUser(userID);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        LogUtil.logDebug(TAG, "onConnectionFailed:" + connectionResult);
    }
}
