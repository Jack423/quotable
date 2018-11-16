package com.apexsoftware.quotable.managers.listeners;

import com.apexsoftware.quotable.model.PostListResult;

public interface OnPostListChangedListener<Post> {

    public void onListChanged(PostListResult result);

    void onCanceled(String message);
}
