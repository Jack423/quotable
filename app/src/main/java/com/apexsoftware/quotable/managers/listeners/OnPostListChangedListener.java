package com.apexsoftware.quotable.managers.listeners;
//Created by Jack Butler on 4/4/2019

import com.apexsoftware.quotable.model.PostListResult;

public interface OnPostListChangedListener<Post> {

    public void onListChanged(PostListResult result);

    void onCanceled(String message);
}
