package com.apexsoftware.quotable.main.postDetails;
// Created by Jack Butler on 10/9/2018.

import android.view.View;

import com.apexsoftware.quotable.main.base.BaseView;
import com.apexsoftware.quotable.model.Comment;
import com.apexsoftware.quotable.model.Post;

import java.util.List;

public interface PostDetailsView extends BaseView {
    void onPostRemoved();

    //void openImageDetailScreen(String imagePath);

    void openProfileActivity(String authorId, View authorView);

    void setQuote(String title);

    void setDescription(String description);

    void setNames(String names);

    //void loadPostDetailImage(String imagePath);

    void loadAuthorPhoto(String photoUrl);

    void setAuthorName(String username);

    void setHandle(String handle);

    void initLikeController(Post post);

    void updateCounters(Post post);

    void initLikeButtonState(boolean exist);

    void showReportMenuAction(boolean show);

    void showEditMenuAction(boolean show);

    void showDeleteMenuAction(boolean show);

    String getCommentText();

    void clearCommentField();

    void scrollToFirstComment();

    void openEditPostActivity(Post post);

    void showCommentProgress(boolean show);

    void showCommentsWarning(boolean show);

    void showCommentsRecyclerView(boolean show);

    void onCommentsListChanged(List<Comment> list);

    void showCommentsLabel(boolean show);
}
