package com.apexsoftware.quotable.main.post.createPost;
// Created by Jack Butler on 10/9/2018.

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.main.post.BaseCreatePostActivity;

import androidx.annotation.NonNull;

public class CreatePostActivity extends BaseCreatePostActivity<CreatePostView, CreatePostPresenter> implements CreatePostView {
    public static final int CREATE_NEW_POST_REQUEST = 11;

    @NonNull
    @Override
    public CreatePostPresenter createPresenter() {
        if (presenter == null) {
            return new CreatePostPresenter(this);
        }
        return presenter;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_post_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.post:
                presenter.doSavePost();
                return true;
                default:
                    return super.onOptionsItemSelected(menuItem);

        }
    }
}
