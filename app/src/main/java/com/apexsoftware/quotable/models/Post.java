package com.apexsoftware.quotable.models;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//Created By: Jack Butler
//Date: 7/22/2018

public class Post {
    private String user;
    private String userId;
    private String text;
    private String userImagePath;
    private Long createdAt;
    private String postId;
    private long bookmarkCount;
    private int itemType;

    //We need an empty constructor for Firebase
    public Post() {

    }

    public Post(String user, String userId, String text, Long createdAt) {
        this.user = user;
        this.userId = userId;
        this.text = text;
        this.createdAt = createdAt;
        this.postId = UUID.randomUUID().toString();
    }

    public String getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public String getPostId() {
        return postId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserImagePath() {
        return userImagePath;
    }

    public Long getBookmarkCount() {
        return bookmarkCount;
    }

    public void setBookmarkCount(Long bookmarkCount) {
        this.bookmarkCount = bookmarkCount;
    }

    public void setUserImagePath(String userImagePath) {
        this.userImagePath = userImagePath;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();

        result.put("user", user);
        result.put("text", text);
        result.put("createdAt", createdAt);
        result.put("postId", postId);
        result.put("userId", userId);

        return result;
    }

    @Override
    public String toString() {
        return "DataYak{" +
                "user='" + user + '\'' +
                ", text='" + text + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
