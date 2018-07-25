package com.apexsoftware.quotable.adapter;
//Created by Jack Butler on 7/25/2018.

import com.apexsoftware.quotable.models.Post;

import java.util.ArrayList;
import java.util.List;

public class PostListResult {
    boolean isMoreDataAvailable;
    List<Post> posts = new ArrayList<>();
    long lastItemCreatedDate;

    public boolean isMoreDataAvailable() {
        return isMoreDataAvailable;
    }

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public long getLastItemCreatedDate() {
        return lastItemCreatedDate;
    }

    public void setLastItemCreatedDate(long lastItemCreatedDate) {
        this.lastItemCreatedDate = lastItemCreatedDate;
    }
}
