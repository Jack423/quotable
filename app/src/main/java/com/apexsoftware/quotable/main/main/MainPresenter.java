package com.apexsoftware.quotable.main.main;

// Created by Jack Butler on 10/5/2018.

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.enums.PostStatus;
import com.apexsoftware.quotable.main.base.BasePresenter;
import com.apexsoftware.quotable.main.postDetails.PostDetailsActivity;
import com.apexsoftware.quotable.managers.PostManager;
import com.apexsoftware.quotable.model.Post;
import com.google.firebase.auth.FirebaseAuth;

public class MainPresenter extends BasePresenter<MainView> {

    private PostManager postManager;
    private DialogFragment dialogFragment;

    public MainPresenter(Context context) {
        super(context);
        postManager = PostManager.getInstance(context);
    }

    void onLaunchHashtagActivityClicked() {
        ifViewAttached(view -> view.openHashtagActivity());
    }

    void onCreatePostClickAction(View anchorView) {
        if (checkInternetConnection(anchorView)) {
            if (checkAuthorization()) {
                ifViewAttached(MainView::openCreatePostActivity);
            }
        }
    }

    void onPostClicked(final Post post, final View postView) {
        postManager.isPostExistSingleValue(post.getId(), exist -> ifViewAttached(view -> {
            if (exist) {
                view.openPostDetailsActivity(post, postView);
            } else {
                view.showFloatButtonRelatedSnackBar(R.string.error_post_was_removed);
            }
        }));
    }

    void onProfileMenuActionClicked() {
        if (checkAuthorization()) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            ifViewAttached(view -> view.openProfileActivity(userId, null));
        }
    }

    void onPostCreated() {
        ifViewAttached(view -> {
            view.refreshPostList();
            view.showFloatButtonRelatedSnackBar(R.string.message_post_was_created);
        });
    }

    void onPostUpdated(Intent data) {
        if (data != null) {
            ifViewAttached(view -> {
                PostStatus postStatus = (PostStatus) data.getSerializableExtra(PostDetailsActivity.POST_STATUS_EXTRA_KEY);
                if (postStatus.equals(PostStatus.REMOVED)) {
                    view.removePost();
                    view.showFloatButtonRelatedSnackBar(R.string.message_post_was_removed);
                } else if (postStatus.equals(PostStatus.UPDATED)) {
                    view.updatePost();
                }
            });
        }
    }

    void updateNewPostCounter() {
        Handler mainHandler = new Handler(context.getMainLooper());
        mainHandler.post(() -> ifViewAttached(view -> {
            int newPostsQuantity = postManager.getNewPostsCounter();
            if (newPostsQuantity > 0) {
                view.showCounterView(newPostsQuantity);
            } else {
                view.hideCounterView();
            }
        }));
    }

    public void initPostCounter() {
        postManager.setPostCounterWatcher(newValue -> updateNewPostCounter());
    }
}
