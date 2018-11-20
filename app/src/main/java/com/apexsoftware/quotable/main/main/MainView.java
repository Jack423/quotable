package com.apexsoftware.quotable.main.main;
// Created by Jack Butler on 10/5/2018.

import android.support.v4.app.DialogFragment;
import android.view.View;

import com.apexsoftware.quotable.main.base.BaseView;
import com.apexsoftware.quotable.model.Post;

public interface MainView extends BaseView {
    void openCreatePostActivity();
    void hideCounterView();
    void openPostDetailsActivity(Post post, View v);
    void showFloatButtonRelatedSnackBar(int messageId);
    void openProfileActivity(String userId, View view);
    void openHashtagActivity();
    void refreshPostList();
    void removePost();
    void updatePost();
    void showCounterView(int count);
}
