package com.apexsoftware.quotable.main.profile;
// Created by Jack Butler on 10/5/2018.

import android.text.Spannable;
import android.view.View;

import com.apexsoftware.quotable.enums.FollowState;
import com.apexsoftware.quotable.main.base.BaseView;
import com.apexsoftware.quotable.model.Post;
import com.apexsoftware.quotable.model.Profile;

public interface ProfileView extends BaseView {
    void showUnfollowConfirmation(Profile profile);
    void updateFollowButtonState(FollowState followState);
    void updateFollowersCount(int count);
    void updateFollowingsCount(int count);
    void setFollowStateChangeResultOk();
    void openPostDetailsActivity(Post post, View postItemView);
    void startEditProfileActivity();
    void openCreatePostActivity();
    void setProfileName(String username);
    void setProfileHandle(String handle);
    void setProfileBio(String bio);
    void setProfileDateJoined(String dateJoined);
    void setProfilePhoto(String photoUrl);
    void setDefaultProfilePhoto();
    void updateLikesCounter(Spannable text);
    void hideLoadingPostsProgress();
    void showLikeCounter(boolean show);
    void updatePostsCounter(Spannable text);
    void showPostCounter(boolean show);
    void onPostRemoved();
    void onPostUpdated();
}
