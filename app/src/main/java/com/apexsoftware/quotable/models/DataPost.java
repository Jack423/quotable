package com.apexsoftware.quotable.models;

import java.util.UUID;

//Created By: Jack Butler
//Date: 7/22/2018

public class DataPost {
    private String user;
    private String userId;
    private String text;
    private Long createdAt;
    private String postId;

    //We need an empty constructor for Firebase
    public DataPost() {

    }

    public DataPost(String user, String userId, String text, Long createdAt) {
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

    @Override
    public String toString() {
        return "DataYak{" +
                "user='" + user + '\'' +
                ", text='" + text + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
