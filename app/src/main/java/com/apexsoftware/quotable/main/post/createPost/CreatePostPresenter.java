package com.apexsoftware.quotable.main.post.createPost;
// Created by Jack Butler on 10/9/2018.

import android.content.Context;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.main.post.BaseCreatePostPresenter;
import com.apexsoftware.quotable.model.Post;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class CreatePostPresenter extends BaseCreatePostPresenter<CreatePostView> {
    public CreatePostPresenter(Context context) {
        super(context);
    }

    @Override
    protected int getSaveFailMessage() {
        return R.string.error_fail_create_post;
    }

    @Override
    protected void savePost(String quote, String description, List<String> tags) {
        ifViewAttached(view -> {
            view.showProgress();
            Post post = new Post();
            post.setQuote(quote);
            post.setDescription(description);

            //post.setNames(names);
            post.setTags(tags);

            post.setAuthorId(FirebaseAuth.getInstance().getCurrentUser().getUid());
            postManager.createPost(post, this);
        });
    }
}
