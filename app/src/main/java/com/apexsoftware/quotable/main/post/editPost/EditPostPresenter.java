package com.apexsoftware.quotable.main.post.editPost;
// Created by Jack Butler on 10/9/2018.

import android.content.Context;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.main.post.BaseCreatePostPresenter;
import com.apexsoftware.quotable.managers.PostManager;
import com.apexsoftware.quotable.managers.listeners.OnPostChangedListener;
import com.apexsoftware.quotable.model.Post;

import java.util.List;

public class EditPostPresenter extends BaseCreatePostPresenter<EditPostView> {
    private Post post;

    EditPostPresenter(Context context) {
        super(context);
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    protected int getSaveFailMessage() {
        return R.string.error_fail_update_post;
    }

    private void updatePostIfChanged(Post updatedPost) {
        if (post.getLikesCount() != updatedPost.getLikesCount()) {
            post.setLikesCount(updatedPost.getLikesCount());
        }

        if (post.getCommentsCount() != updatedPost.getCommentsCount()) {
            post.setCommentsCount(updatedPost.getCommentsCount());
        }

        if (post.getWatchersCount() != updatedPost.getWatchersCount()) {
            post.setWatchersCount(updatedPost.getWatchersCount());
        }

        if (post.isHasReport() != updatedPost.isHasReport()) {
            post.setHasReport(updatedPost.isHasReport());
        }
    }

    @Override
    protected void savePost(String quote, String description, List<String> tags) {
        ifViewAttached(view -> {
            view.showProgress(R.string.message_saving);

            post.setQuote(quote);
            post.setDescription(description);
            //post.setNames(names);
            post.setTags(tags);

            postManager.createOrUpdatePost(post);
            onPostSaved(true);
        });
    }

    public void addCheckIsPostChangedListener() {
        PostManager.getInstance(context.getApplicationContext()).getPost(context, post.getId(), new OnPostChangedListener() {
            @Override
            public void onObjectChanged(Post obj) {
                if (obj == null) {
                    ifViewAttached(view -> view.showWarningDialog(R.string.error_post_was_removed, (dialog, which) -> {
                        view.openMainActivity();
                        view.finish();
                    }));
                } else {
                    updatePostIfChanged(obj);
                }
            }

            @Override
            public void onError(String errorText) {
                ifViewAttached(view -> view.showWarningDialog(errorText, (dialog, which) -> {
                    view.openMainActivity();
                    view.finish();
                }));
            }
        });
    }

    public void closeListeners() {
        postManager.closeListeners(context);
    }
}
